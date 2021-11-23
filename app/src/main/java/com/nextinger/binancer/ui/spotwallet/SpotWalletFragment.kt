package com.nextinger.binancer.ui.spotwallet


import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.nextinger.binancer.client.binance.dto.AccountInfo
import com.nextinger.binancer.client.binance.dto.CoinInfo
import com.nextinger.binancer.client.binance.dto.SymbolExchangeRate
import com.nextinger.binancer.data.database.entity.KnownSymbol
import com.nextinger.binancer.data.objects.Asset
import com.nextinger.binancer.data.objects.NetworkResult
import com.nextinger.binancer.data.objects.enums.DisplayCurrency
import com.nextinger.binancer.data.objects.enums.SortBy
import com.nextinger.binancer.data.objects.filter.SpotWalletFilter
import com.nextinger.binancer.databinding.FragmentSpotWalletBinding
import com.nextinger.binancer.models.BinanceViewModel
import com.nextinger.binancer.models.MainViewModel
import com.nextinger.binancer.models.SettingsModel
import com.nextinger.binancer.ui.MainActivity
import com.nextinger.binancer.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SpotWalletFragment : Fragment() {

    private lateinit var binding: FragmentSpotWalletBinding
    private lateinit var binanceViewModel: BinanceViewModel
    private lateinit var mainViewModel: MainViewModel
    private val mAdapter by lazy { BalanceItemAdapter() }
    private lateinit var mainHandler: Handler

    private var assetMap: MutableMap<String, Asset> = mutableMapOf()
    private var symbolNameMap: Map<String, String>? = mutableMapOf()
    private lateinit var filter: SpotWalletFilter

    private val currencyNumberFormat = NumberFormat.getCurrencyInstance(Locale.US)


    private val tickerTask = object : Runnable {
        override fun run() {
            binanceViewModel.getAllSymbolPriceTicker()
            mainHandler.postDelayed(this, TimeUnit.SECONDS.toMillis(filter.tickerRefreshDelay))
        }
    }

    private val accountInfoTask = object : Runnable {
        override fun run() {
            fetchAccountInfo()
            mainHandler.postDelayed(this, TimeUnit.SECONDS.toMillis(filter.accountRefreshDelay))
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpotWalletBinding.inflate(layoutInflater)
        binanceViewModel = ViewModelProvider(this)[BinanceViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainHandler = Handler(Looper.getMainLooper())

        // setup balances recycler view
        binding.balanceRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        // register observers
        binanceViewModel.allCoinsInformationResult.observe(viewLifecycleOwner, { result ->
            handleAllCoinInformationResult(result)
        })

        binanceViewModel.accountInfoInfoResult.observe(viewLifecycleOwner, { result ->
            handleAccountInfoResult(result)
        })
        binanceViewModel.allSymbolPriceTickerResult.observe(viewLifecycleOwner, { result ->
            handleAllSymbolPrices(result)
        })

        // set filter change
        SettingsModel.changes.onEach {
            when(it.setting.key) {
                SettingsModel.DISPLAY_CURRENCY -> {
                    filter.displayCurrency = it.value as DisplayCurrency
                    restartAccountTickerLoading()
                }
                SettingsModel.SPOT_SORT_BY -> {
                    filter.sortBy = it.value as SortBy
                    updateAdapter()
                    binding.balanceRecyclerView.smoothScrollToPosition(0)   // on sort scroll to top
                }
                SettingsModel.SPOT_TICKER_REFRESH_DELAY -> {
                    filter.tickerRefreshDelay = it.value as Long
                    restartAccountTickerLoading()
                }
                SettingsModel.SPOT_ACCOUNT_REFRESH_DELAY -> {
                    filter.accountRefreshDelay = it.value as Long
                    restartAccountTickerLoading()
                }
                SettingsModel.SPOT_HIDE_DUST -> {
                    filter.hideDust = it.value as Boolean
                    updateAdapter()
                }
            }
        }.launchIn(lifecycleScope)

        // TODO
        (activity as MainActivity).setDropdownSettingsFragment(FilterSpotWalletFragment())

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // init filter
        lifecycleScope.launch(Dispatchers.IO) {
            val currency = SettingsModel.displayCurrency.value
            val sortBy = SettingsModel.spotSortBy.value
            val tickerDelay = SettingsModel.spotTickerRefreshDelay.value
            val accountDelay = SettingsModel.spotAccountRefreshDelay.value
            val hideDust = SettingsModel.spotHideDust.value

            withContext(Dispatchers.Main) {
                filter = SpotWalletFilter(currency, sortBy, tickerDelay, accountDelay, hideDust)
                // start loading
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
        mainHandler.removeCallbacks(accountInfoTask)
    }

    /**
     * Stop account and ticker tasks then start them again
     */
    private fun restartAccountTickerLoading() {
        // stop current tasks
        stopTasks()
        // start account task
        mainHandler.post(accountInfoTask)
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
                // start account loading task
                mainHandler.post(accountInfoTask)
            } is NetworkResult.Error -> {
                mainViewModel.error(result.message)
            } else -> {}
        }
    }

    /**
     * Fetch account info
     */
    private fun fetchAccountInfo() {
        binanceViewModel.getAccountInfo()
    }

    /**
     * Handle AccountInfo result and
     */
    private fun handleAccountInfoResult(result: NetworkResult<AccountInfo>) {
        when(result) {
            is NetworkResult.Success -> {
                val balanceList = result.data!!.balances  // always data on success

                for(asset in assetMap.values.toList()) {
                    asset.valid = false
                }

                for (balance in balanceList) {
                    val symbol = balance.asset
                    val totalAmount = (balance.free + balance.locked)

                    if (totalAmount > 0) {
                        val existingAsset = assetMap[symbol]

                        existingAsset?.let {
                            it.totalAmount = totalAmount
                            it.valid = true
                        } ?: run {
                            val name = symbolNameMap?.let { it[symbol] } ?: symbol
                            assetMap[symbol] = Asset(symbol, name, totalAmount, null, true)
                            binanceViewModel.insertKnowSymbol(KnownSymbol(symbol, name))
                        }
                    }
                }

                for(asset in assetMap.values.toList()) {
                    if(!asset.valid)
                        assetMap.remove(asset.symbol)
                }

                updateAdapter()

                if(!HandlerCompat.hasCallbacks(mainHandler,tickerTask)) {
                    mainHandler.post(tickerTask)
                }
            }
            is NetworkResult.Error -> {
                mainViewModel.error(result.message)
            }
            is NetworkResult.Loading -> {}
        }
    }

    /**
     * Apply filters and update adapter with current data
     */
    private fun updateAdapter() {
        var list = assetMap.values.toList()

        if (filter.hideDust) {
            list = list.filter { (it.getValue() ?: 0f) > Constants.DUST_THRESHOLD}
        }

        list = when(filter.sortBy) {
            SortBy.NAME_ASC -> list.sortedBy { it.symbol }
            else -> list.sortedByDescending { it.getValue() } // SortBy.VALUE_ASC - default
        }

        mAdapter.setAssetBalanceList(list)
    }

    /**
     * Handle all symbol prices
     */
    private fun handleAllSymbolPrices(result: NetworkResult<List<SymbolExchangeRate>>) {
        when(result) {
            is NetworkResult.Success -> {
                val currentExchangeRates = result.data!!    // always data on success

                var eurUsdtExchangeRate: Float? = null

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
                        val asset = assetMap[symbol]
                        if (asset != null) {
                            // set current USDT unit price
                            asset.unitPrice = exchangeRate.price
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

                var totalValue = 0.0f
                for (asset in assetMap.values) {
                    if(filter.displayCurrency == DisplayCurrency.EUR) {
                        asset.unitPrice = asset.unitPrice?.div(eurUsdtExchangeRate!!)
                    }

                    asset.getValue()?.let {
                        totalValue += it
                    }
                }

                binding.totalAmountTextView.text = currencyNumberFormat.format(totalValue)
                updateAdapter()
                mainViewModel.content()
            } is NetworkResult.Error -> {
                mainViewModel.error(result.message)
            } else -> {
            }
        }
    }
}