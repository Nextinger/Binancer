package com.nextinger.binancer.ui.tradehistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.nextinger.binancer.data.objects.enums.SortBy
import com.nextinger.binancer.data.objects.filter.TradeFilter
import com.nextinger.binancer.databinding.FragmentFilterTradeHistoryBinding
import com.nextinger.binancer.models.SettingsModel
import com.nextinger.binancer.ui.FilterFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterTradeHistoryFragment : FilterFragment<TradeFilter>() {

    private lateinit var binding: FragmentFilterTradeHistoryBinding


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterTradeHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initFilter(): TradeFilter {
        val sortBy = SettingsModel.tradeSortBy.value
        return TradeFilter(sortBy)
    }

    override fun initItemsValues(filter: TradeFilter) {
        when(filter.sortBy) {
            SortBy.TIME_ASC -> {binding.oldestRadioButton.isChecked = true}
            else -> {binding.newestRadioButton.isChecked = true}   // SortBy.TIME_DESC - default
        }
    }

    override fun initItemsListeners() {
        binding.newestRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.tradeSortBy.update(SortBy.NAME_ASC)
            }
        }

        binding.oldestRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.tradeSortBy.update(SortBy.VALUE_DESC)
            }
        }
    }

}