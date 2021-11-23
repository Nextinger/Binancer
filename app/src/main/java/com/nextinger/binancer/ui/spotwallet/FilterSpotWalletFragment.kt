package com.nextinger.binancer.ui.spotwallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import com.nextinger.binancer.databinding.FragmentFilterSpotWalletBinding
import com.nextinger.binancer.models.SettingsModel
import com.nextinger.binancer.data.objects.enums.DisplayCurrency
import com.nextinger.binancer.data.objects.enums.SortBy
import com.nextinger.binancer.data.objects.filter.SpotWalletFilter
import com.nextinger.binancer.ui.FilterFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterSpotWalletFragment : FilterFragment<SpotWalletFilter>() {

    private lateinit var binding: FragmentFilterSpotWalletBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterSpotWalletBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initFilter(): SpotWalletFilter {
        val currency = SettingsModel.displayCurrency.value
        val sortBy = SettingsModel.spotSortBy.value
        val tickerDelay = SettingsModel.spotTickerRefreshDelay.value
        val accountDelay = SettingsModel.spotAccountRefreshDelay.value
        val hideDust = SettingsModel.spotHideDust.value
        return SpotWalletFilter(currency, sortBy, tickerDelay, accountDelay, hideDust)
    }

    override fun initItemsValues(filter: SpotWalletFilter) {

        when(filter.displayCurrency) {
            DisplayCurrency.USDT -> {binding.usdtRadioButton.isChecked = true}
            DisplayCurrency.EUR -> {binding.eurRadioButton.isChecked = true}
        }

        when(filter.sortBy) {
            SortBy.NAME_ASC -> {binding.nameRadioButton.isChecked = true}
            else -> {binding.highestRadioButton.isChecked = true}   // SortBy.VALUE_DESC - default
        }

        binding.accountRefreshSlider.value = filter.accountRefreshDelay.toFloat()
        binding.tickerRefreshSlider.value = filter.tickerRefreshDelay.toFloat()
        binding.hideDustCheckBox.isChecked = filter.hideDust
    }

    override fun initItemsListeners() {
        //
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

        binding.nameRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.spotSortBy.update(SortBy.NAME_ASC)
            }
        }

        binding.highestRadioButton.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) SettingsModel.spotSortBy.update(SortBy.VALUE_DESC)
            }
        }

        binding.accountRefreshSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                lifecycleScope.launch(Dispatchers.IO) {
                    SettingsModel.spotAccountRefreshDelay.update(slider.value.toLong())
                }
            }
        })

        binding.tickerRefreshSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                lifecycleScope.launch(Dispatchers.IO) {
                    SettingsModel.spotTickerRefreshDelay.update(slider.value.toLong())
                }
            }
        })

        binding.hideDustCheckBox.setOnCheckedChangeListener{ _, isChecked ->
            lifecycleScope.launch(Dispatchers.IO) {
                SettingsModel.spotHideDust.update(isChecked)
            }
        }

    }

}