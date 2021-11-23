package com.nextinger.binancer.client.binance.dto

data class AssetBalance(
    val asset: String,
    val free: Float,
    val locked: Float
)