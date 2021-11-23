package com.nextinger.binancer.ui.tradehistory

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
import com.nextinger.binancer.client.binance.dto.Trade
import com.nextinger.binancer.data.objects.NetworkResult
import com.nextinger.binancer.data.objects.enums.SortBy
import com.nextinger.binancer.data.objects.filter.TradeFilter
import com.nextinger.binancer.databinding.FragmentTradeHistoryBinding
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

@AndroidEntryPoint
class TradeHistoryFragment : Fragment() {

    private lateinit var binding: FragmentTradeHistoryBinding
    private lateinit var binanceViewModel: BinanceViewModel
    private val mAdapter by lazy { TradeItemAdapter() }
    private lateinit var mainViewModel: MainViewModel
    private lateinit var filter: TradeFilter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTradeHistoryBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binanceViewModel = ViewModelProvider(this)[BinanceViewModel::class.java]

        // setup order recycler view
        binding.tradesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        // register observer
        binanceViewModel.tradesResult.observe(viewLifecycleOwner, { result ->
            handleTradesResult(result)
        })

        // set filter change
        SettingsModel.changes.onEach {
            when(it.setting.key) {
                SettingsModel.TRADE_SORT_BY -> {
                    filter.sortBy = it.value as SortBy
                    // TODO
                    // binding.orderRecyclerView.smoothScrollToPosition(0)   // on sort scroll to top
                }
            }
        }.launchIn(lifecycleScope)

        // TODO
        (activity as MainActivity).setDropdownSettingsFragment(FilterTradeHistoryFragment())

        fetchTrades()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // init filter
        lifecycleScope.launch(Dispatchers.IO) {
            val sortBy = SettingsModel.orderSortBy.value

            withContext(Dispatchers.Main) {
                filter = TradeFilter(sortBy)
                fetchTrades()
            }
        }
    }

    /**
     * Fetch trades
     */
    fun fetchTrades() {
        mainViewModel.loading()
        binanceViewModel.getTrades("BTCUSDT", null, null)
    }

    private fun handleTradesResult(result: NetworkResult<List<Trade>>) {
        when (result) {
            is NetworkResult.Success -> {
                mAdapter.setTrades(result.data!!)
                //mainViewModel.content()

                // just for fun now
                Handler(Looper.getMainLooper()).postDelayed({
                    mainViewModel.noResult("No trades")
                }, 1200)
            }
            is NetworkResult.Error -> {
                mainViewModel.error(result.message)
            }
            is NetworkResult.Loading -> {

            }
        }
    }




}