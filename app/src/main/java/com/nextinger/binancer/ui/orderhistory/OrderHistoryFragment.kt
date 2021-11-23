package com.nextinger.binancer.ui.orderhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.nextinger.binancer.client.binance.dto.Order
import com.nextinger.binancer.data.objects.NetworkResult
import com.nextinger.binancer.data.objects.enums.SortBy
import com.nextinger.binancer.data.objects.filter.OrderFilter
import com.nextinger.binancer.databinding.FragmentOrderHistoryBinding
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
class OrderHistoryFragment : Fragment() {

    private lateinit var binding: FragmentOrderHistoryBinding
    private lateinit var binanceViewModel: BinanceViewModel
    private lateinit var mainViewModel: MainViewModel
    private val mAdapter by lazy { OrderItemAdapter() }

    private lateinit var filter: OrderFilter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderHistoryBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binanceViewModel = ViewModelProvider(this)[BinanceViewModel::class.java]

        // setup order recycler view
        binding.ordersRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        // register observer
        binanceViewModel.ordersResult.observe(viewLifecycleOwner, { result ->
            handleOrdersResult(result)
        })

        // set filter change
        SettingsModel.changes.onEach {
            when(it.setting.key) {
                SettingsModel.ORDER_SORT_BY -> {
                    filter.sortBy = it.value as SortBy
                    // TODO
                    // binding.orderRecyclerView.smoothScrollToPosition(0)   // on sort scroll to top
                }
            }
        }.launchIn(lifecycleScope)


        // TODO
        (activity as MainActivity).setDropdownSettingsFragment(FilterOrderHistoryFragment())

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // init filter
        lifecycleScope.launch(Dispatchers.IO) {
            val sortBy = SettingsModel.orderSortBy.value

            withContext(Dispatchers.Main) {
                filter = OrderFilter(sortBy)
                fetchOrders()
            }
        }
    }

    private fun fetchOrders() {
        mainViewModel.loading()
        binanceViewModel.getOrders("BTCUSDT", null, null)
    }

    private fun handleOrdersResult(result: NetworkResult<List<Order>>) {

        when (result) {
            is NetworkResult.Success -> {
                var list = result.data!!
                list = list.sortedByDescending { it.time }
                mAdapter.setOrders(list)   // always data on success
                mainViewModel.content()
            }
            is NetworkResult.Error -> {
                mainViewModel.error(result.message)
            }
            is NetworkResult.Loading -> {

            }
        }

    }

}