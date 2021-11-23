package com.nextinger.binancer.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nextinger.binancer.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class BindingAdapters {

    companion object {

        @BindingAdapter("setNumber")
        @JvmStatic
        fun setNumber(textView: TextView, value: Float) {
            textView.text = String.format("%f",value)
        }

        @BindingAdapter("setBalanceValue")
        @JvmStatic
        fun setBalanceValue(textView: TextView, value: Float) {
            textView.text = String.format("%.8f", value)
        }

        @BindingAdapter("setFirstCapital")
        @JvmStatic
        fun setFirstCapital(textView: TextView, value: String) {
            textView.text = setFirstCapital(value)
        }

        @BindingAdapter("setCurrencyValue")
        @JvmStatic
        fun setCurrencyValue(textView: TextView, value: Float) {
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            textView.text = format.format(value)
        }

        @BindingAdapter("setDate")
        @JvmStatic
        fun setDate(textView: TextView, time: Long) {
            val date = Date(time)
            val sdf = SimpleDateFormat("dd.MM.yyyy hh:mm:ss")

            textView.text = sdf.format(date)
        }


        @BindingAdapter("setSymbolImage")
        @JvmStatic
        fun setSymbolImage(imageView: ImageView, symbol: String) {

            var resId = imageView.resources.getIdentifier(
                symbol.lowercase(),
                "drawable",
                imageView.context.packageName
            )
            if (resId == 0) {
                resId = R.drawable.generic_coin
            }

            imageView.setImageDrawable(imageView.resources.getDrawable(resId, null))
        }

        private fun setFirstCapital(value: String): String {
            return value[0].uppercase() + value.drop(1).lowercase()
        }

    }

}