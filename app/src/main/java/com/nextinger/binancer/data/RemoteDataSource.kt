package com.nextinger.binancer.data

import com.nextinger.binancer.client.binance.BinanceApi
import com.nextinger.binancer.client.binance.dto.*
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val binanceApi: BinanceApi
) {

    private val recvWindow = 60000L

    suspend fun getAllSymbolPriceTicker(): Response<List<SymbolExchangeRate>> {
        return binanceApi.getSymbolPriceTicker(null)
    }

    suspend fun getAccountInfo(): Response<AccountInfo> {
        return binanceApi.getAccountInfo(recvWindow, Date().time)
    }

    suspend fun getAllCoinsInformation(): Response<List<CoinInfo>> {
        return binanceApi.getAllCoinsInformation(recvWindow, Date().time)
    }

    suspend fun getOrders(symbol: String, orderId: Long?, limit: Int?): Response<List<Order>> {
        return binanceApi.getAllOrders(symbol, orderId, limit, recvWindow, Date().time)
    }

    suspend fun getTrades(symbol: String, orderId: Long?, limit: Int?): Response<List<Trade>> {
        return binanceApi.getTrades(
            symbol,
            orderId,
            null,
            null,
            null,
            limit,
            recvWindow,
            Date().time
        )
    }

}