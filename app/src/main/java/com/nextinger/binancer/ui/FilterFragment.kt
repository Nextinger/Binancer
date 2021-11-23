package com.nextinger.binancer.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class FilterFragment<T: Any> : Fragment() {

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch(Dispatchers.IO) {
            val filter = initFilter()

            withContext(Dispatchers.Main) {
                initItemsValues(filter)
                initItemsListeners()
            }
        }
    }

    abstract fun initFilter(): T

    abstract fun initItemsValues(filter: T)

    abstract fun initItemsListeners()

}