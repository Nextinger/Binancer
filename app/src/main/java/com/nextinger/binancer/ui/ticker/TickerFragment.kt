package com.nextinger.binancer.ui.ticker

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.nextinger.binancer.client.binance.dto.CoinInfo
import com.nextinger.binancer.client.binance.dto.SymbolExchangeRate
import com.nextinger.binancer.data.objects.Asset
import com.nextinger.binancer.data.objects.NetworkResult
import com.nextinger.binancer.data.objects.enums.DisplayCurrency
import com.nextinger.binancer.data.objects.enums.SortBy
import com.nextinger.binancer.data.objects.filter.TickerFilter
import com.nextinger.binancer.databinding.FragmentTickerBinding
import com.nextinger.binancer.models.BinanceViewModel
import com.nextinger.binancer.models.MainViewModel
import com.nextinger.binancer.models.SettingsModel
import com.nextinger.binancer.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class TickerFragment : Fragment() {

    private lateinit var binding: FragmentTickerBinding
    private lateinit var binanceViewModel: BinanceViewModel
    private lateinit var mainViewModel: MainViewModel
    private val mAdapter by lazy { AssetItemAdapter() }

    private var assetMap: MutableMap<String, Asset> = mutableMapOf()
    private var symbolNameMap: Map<String, String>? = mutableMapOf()
    private lateinit var filter: TickerFilter
    private lateinit var mainHandler: Handler
    private val currencyNumberFormat = NumberFormat.getCurrencyInstance(Locale.US)


    private val tickerTask = object : Runnable {
        override fun run() {
            binanceViewModel.getAllSymbolPriceTicker()
            mainHandler.postDelayed(this, TimeUnit.SECONDS.toMillis(filter.tickerRefreshDelay))
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentTickerBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binanceViewModel = ViewModelProvider(this)[BinanceViewModel::class.java]

        // setup ticker recycler view
        binding.tickerRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        // register observers
        binanceViewModel.allCoinsInformationResult.observe(viewLifecycleOwner, { result ->
            handleAllCoinInformationResult(result)
        })

        binanceViewModel.allSymbolPriceTickerResult.observe(viewLifecycleOwner, { result ->
            handleAllSymbolPriceList(result)
        })

        // set filter change
        SettingsModel.changes.onEach {
            when(it.setting.key) {
                SettingsModel.DISPLAY_CURRENCY -> {
                    filter.displayCurrency = it.value as DisplayCurrency
                    restartAccountTickerLoading()
                }
                SettingsModel.TICKER_SORT_BY -> {
                    filter.sortBy = it.value as SortBy
                    updateAdapter()
                    binding.tickerRecyclerView.smoothScrollToPosition(0)   // on sort scroll to top
                }
                SettingsModel.TICKER_TICKER_REFRESH_DELAY -> {
                    filter.tickerRefreshDelay = it.value as Long
                    restartAccountTickerLoading()
                }
            }
        }.launchIn(lifecycleScope)

        // TODO
        (activity as MainActivity).setDropdownSettingsFragment(FilterTickerFragment())

        mainHandler = Handler(Looper.getMainLooper())

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // init filter
        lifecycleScope.launch(Dispatchers.IO) {
            val currency = SettingsModel.displayCurrency.value
            val sortBy = SettingsModel.tickerSortBy.value
            val tickerDelay = SettingsModel.tickerTickerRefreshDelay.value

            withContext(Dispatchers.Main) {
                filter = TickerFilter(currency, sortBy, tickerDelay)
                fetchAllCoinsInformation()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopTasks()
    }

    /**
     * Stop current tasks
     */
    private fun stopTasks() {
        mainHandler.removeCallbacks(tickerTask)
    }

    /**
     * Stop account and ticker tasks then start them again
     */
    private fun restartAccountTickerLoading() {
        // stop current tasks
        stopTasks()
        // start account task
        mainHandler.post(tickerTask)
    }

    /**
     * Fetch all coins information
     */
    private fun fetchAllCoinsInformation() {
        mainViewModel.loading()
        binanceViewModel.getAllCoinsInformation()
    }

    /**
     * Handle AllCoinInformation result and initialize symbol name map
     */
    private fun handleAllCoinInformationResult(result: NetworkResult<List<CoinInfo>>) {
        when(result) {
            is NetworkResult.Success -> {
                // create symbol name map
                symbolNameMap = result.data?.map{ it.coin to it.name }?.toMap()
                // start ticker loading task
                mainHandler.post(tickerTask)

            } is NetworkResult.Error -> {
                mainViewModel.error(result.message)
            } else -> {
                // show progress
            }
        }
    }

    /**
     * Handle all symbol prices result
     */
    private fun handleAllSymbolPriceList(result: NetworkResult<List<SymbolExchangeRate>>) {
        when(result) {
            is NetworkResult.Success -> {
                val currentExchangeRates = result.data!!    // always data on success

                var eurUsdtExchangeRate: Float? = null

                // TODO add valid check?

                for (exchangeRate in currentExchangeRates) {
                    // get only USDT exchangeable symbols
                    if (exchangeRate.symbol.endsWith("USDT")) {
                        // replace USDT to get symbol
                        val symbol = exchangeRate.symbol.replace("USDT","")
                        // store USDT EUR exchange rate
                        if (symbol == "EUR") {
                            eurUsdtExchangeRate = exchangeRate.price
                        }
                        // find symbol in assets
                        var asset = assetMap[symbol]
                        if (asset != null) {
                            // set current USDT unit price
                            asset.unitPrice = exchangeRate.price
                        } else {
                            val name = symbolNameMap?.let { it[symbol] } ?: symbol
                            asset = Asset(symbol, name, null, exchangeRate.price)
                            assetMap.put(symbol, asset)
                        }
                    }
                }

                // set USDT unit price to 1 as a base currency
                assetMap["USDT"]?.unitPrice = 1f

                if(filter.displayCurrency == DisplayCurrency.EUR){
                    if (eurUsdtExchangeRate == null) {
                        //TODO shouldn't happened
                        return
                    }

                    val eurCurrency = Currency.getInstance("EUR")
                    currencyNumberFormat.currency = eurCurrency
                } else {
                    val usdCurrency = Currency.getInstance("USD")
                    currencyNumberFormat.currency = usdCurrency
                }

                Asset.currencyNumberFormat = currencyNumberFormat

                if(filter.displayCurrency == DisplayCurrency.EUR) {
                    //asset.unitPrice = asset.unitPrice?.div(eurUsdtExchangeRate!!)
                    assetMap.values.map {
                        it.unitPrice = it.unitPrice?.div(eurUsdtExchangeRate!!)
                    }
                }

                updateAdapter()
                mainViewModel.content()
            } is NetworkResult.Error -> {
                mainViewModel.error(result.message)
            } else -> {
                // show progress
            }
        }
    }

    /**
     * Apply filters and update adapter with current data
     */
    private fun updateAdapter() {
        var list = assetMap.values.toList()

        list = when(filter.sortBy) {
            SortBy.NAME_ASC -> list.sortedBy { it.symbol }
            else -> list.sortedByDescending { it.unitPrice } // SortBy.VALUE_DESC - default
        }

        mAdapter.setAssetList(list) // todo
    }





}