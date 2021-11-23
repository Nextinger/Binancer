package com.nextinger.binancer.models

import com.michaelflisar.materialpreferences.core.SettingsModel
import com.michaelflisar.materialpreferences.datastore.DataStoreStorage
import com.nextinger.binancer.data.objects.enums.DisplayCurrency
import com.nextinger.binancer.data.objects.enums.SortBy
import com.nextinger.binancer.utils.Constants


object SettingsModel : SettingsModel(DataStoreStorage(Constants.DATASTORE_SETTINGS)) {

    // global
    const val API_KEY = "api_key"
    val apiKey by stringPref("", API_KEY)
    const val API_SECRET = "api_secret"
    val apiSecret by stringPref("", API_SECRET)

    const val DARK_MODE = "display_currency"
    val darkMode by boolPref(true, DARK_MODE)
    const val DISPLAY_CURRENCY = "display_currency"
    val displayCurrency by enumPref(DisplayCurrency.USDT, DISPLAY_CURRENCY)

    // spot wallet
    const val SPOT_SORT_BY = "spot_sort_by"
    val spotSortBy by enumPref(SortBy.VALUE_DESC, SPOT_SORT_BY)

    const val SPOT_TICKER_REFRESH_DELAY = "spot_ticker_delay"
    val spotTickerRefreshDelay by longPref(10, SPOT_TICKER_REFRESH_DELAY)

    const val SPOT_ACCOUNT_REFRESH_DELAY = "spot_account_delay"
    val spotAccountRefreshDelay by longPref(60, SPOT_ACCOUNT_REFRESH_DELAY)

    const val SPOT_HIDE_DUST = "spot_hide_dust"
    val spotHideDust by boolPref(false, SPOT_HIDE_DUST)

    // ticker
    const val TICKER_SORT_BY = "ticker_sort_by"
    val tickerSortBy by enumPref(SortBy.VALUE_DESC, TICKER_SORT_BY)

    const val TICKER_TICKER_REFRESH_DELAY = "ticker_ticker_delay"
    val tickerTickerRefreshDelay by longPref(10, TICKER_TICKER_REFRESH_DELAY)

    // order history
    const val ORDER_SORT_BY = "order_sort_by"
    val orderSortBy by enumPref(SortBy.TIME_DESC, ORDER_SORT_BY)

    // trade history
    const val TRADE_SORT_BY = "trade_sort_by"
    val tradeSortBy by enumPref(SortBy.TIME_DESC, TRADE_SORT_BY)

}