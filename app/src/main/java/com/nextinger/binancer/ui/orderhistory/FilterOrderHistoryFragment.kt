package com.nextinger.binancer.ui.orderhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.nextinger.binancer.data.objects.enums.SortBy
import com.nextinger.binancer.data.objects.filter.OrderFilter
import com.nextinger.binancer.databinding.FragmentFilterOrderHistoryBinding
import com.nextinger.binancer.models.SettingsModel
import com.nextinger.binancer.ui.FilterFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterOrderHistoryFragment : FilterFragment<OrderFilter>() {

    private lateinit var binding: FragmentFilterOrderHistoryBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterOrderHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initFilter(): OrderFilter {
        val sortBy = SettingsModel.orderSortBy.value
        return OrderFilter(sortBy)
    }

    override fun initItemsValues(filter: OrderFilter) {
        when(filter.sortBy) {
            SortBy.TIME_ASC -> {binding.oldestRadioButton.isChecked = true}
            else -> {binding.newestRadioButton.isChecked = true}   // SortBy.TIME_DESC - default
        }
    }

    override fun initItemsListeners() {
        binding.newestRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.orderSortBy.update(SortBy.NAME_ASC)
            }
        }

        binding.oldestRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.orderSortBy.update(SortBy.VALUE_DESC)
            }
        }
    }

}