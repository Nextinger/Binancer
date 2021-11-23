package com.nextinger.binancer.data.objects

import android.icu.text.NumberFormat
import com.nextinger.binancer.utils.Utils


data class Asset(
    val symbol: String,
    val name: String?,
    var totalAmount: Float?,
    var unitPrice: Float?,
    var valid: Boolean = true
) {

    fun getValue(): Float? {
        return Utils.safeLet(unitPrice, totalAmount) { unitPrice, totalAmount ->
            unitPrice * totalAmount
        }
    }

    fun getFormattedValue(): String {
        return getValue()?.let {
            currencyNumberFormat?.format(it) ?: it.toString()
        } ?: ""
    }

    fun getFormattedUnitPrice(): String {
        return unitPrice?.let {
            currencyNumberFormat?.format(it) ?: it.toString()
        } ?: ""
    }

    companion object {
        var currencyNumberFormat: NumberFormat? = null
    }

}