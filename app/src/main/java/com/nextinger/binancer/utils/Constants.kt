package com.nextinger.binancer.utils

class Constants {

    companion object {
        // GENERAL
        const val DUST_THRESHOLD = 0.1f // in $ USDT
        //
        const val BINANCE_BASE_URL = "https://api.binance.com"
        // DATASTORE
        const val DATASTORE_SETTINGS = "datastore_setting"
        // DATABASE
        const val DATABASE_NAME = "binance_database"
        const val ORDERS_TABLE = "orders_table"
        const val KNOWN_SYMBOLS_TABLE = "known_symbols_table"
        // UI
        const val FADE_ANIMATION_DURATION = 400L
    }

}