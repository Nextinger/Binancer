package com.nextinger.binancer.client.binance

import com.nextinger.binancer.client.binance.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface BinanceApi {

    @GET("/api/v3/ticker/price")
    suspend fun getSymbolPriceTicker(
        @Query("symbol") symbol: String?
    ): Response<List<SymbolExchangeRate>>

    @Headers(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/api/v3/account")
    suspend fun getAccountInfo(
        @Query("recvWindow") recvWindow: Long?,
        @Query("timestamp") timestamp: Long
    ): Response<AccountInfo>

    @Headers(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/sapi/v1/capital/config/getall")
    suspend fun getAllCoinsInformation(
        @Query("recvWindow") recvWindow: Long?,
        @Query("timestamp") timestamp: Long
    ): Response<List<CoinInfo>>

    @Headers(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
	@GET("/api/v3/allOrders")
	suspend fun getAllOrders(
		@Query("symbol") symbol: String,
		@Query("orderId") orderId: Long?,
		@Query("limit") limit: Int?,
		@Query("recvWindow") recvWindow: Long?,
		@Query("timestamp") timestamp: Long
	): Response<List<Order>>

    @Headers(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/api/v3/myTrades")
    suspend fun getTrades(
        @Query("symbol") symbol: String,
        @Query("orderId") orderId: Long?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
        @Query("fromId") fromId: Long?,
        @Query("limit") limit: Int?,
        @Query("recvWindow") recvWindow: Long?,
        @Query("timestamp") timestamp: Long
    ): Response<List<Trade>>

}