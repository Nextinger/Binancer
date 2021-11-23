package com.nextinger.binancer.ui.ticker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import com.nextinger.binancer.data.objects.enums.DisplayCurrency
import com.nextinger.binancer.data.objects.enums.SortBy
import com.nextinger.binancer.data.objects.filter.TickerFilter
import com.nextinger.binancer.databinding.FragmentFilterTickerBinding
import com.nextinger.binancer.models.SettingsModel
import com.nextinger.binancer.ui.FilterFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterTickerFragment : FilterFragment<TickerFilter>() {

    private lateinit var binding: FragmentFilterTickerBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterTickerBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun initFilter(): TickerFilter {
        val currency = SettingsModel.displayCurrency.value
        val sortBy = SettingsModel.tickerSortBy.value
        val tickerDelay = SettingsModel.tickerTickerRefreshDelay.value
        return TickerFilter(currency, sortBy, tickerDelay)
    }

    override fun initItemsValues(filter: TickerFilter) {

        when(filter.displayCurrency) {
            DisplayCurrency.USDT -> {binding.usdtRadioButton.isChecked = true}
            DisplayCurrency.EUR -> {binding.eurRadioButton.isChecked = true}
        }

        when(filter.sortBy) {
            SortBy.NAME_ASC -> {binding.nameRadioButton.isChecked = true}
            else -> {binding.highestRadioButton.isChecked = true}   // SortBy.VALUE_DESC - default
        }

        binding.tickerRefreshSlider.value = filter.tickerRefreshDelay.toFloat()
    }

    override fun initItemsListeners() {
        binding.usdtRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.displayCurrency.update(DisplayCurrency.USDT)
            }
        }

        binding.eurRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.displayCurrency.update(DisplayCurrency.EUR)
            }
        }

        binding.highestRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.tickerSortBy.update(SortBy.VALUE_DESC)
            }
        }

        binding.nameRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.tickerSortBy.update(SortBy.NAME_ASC)
            }
        }



        binding.tickerRefreshSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                lifecycleScope.launch(Dispatchers.IO) {
                    SettingsModel.tickerTickerRefreshDelay.update(slider.value.toLong())
                }
            }
        })


    }


}