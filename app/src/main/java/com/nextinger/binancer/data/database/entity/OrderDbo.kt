package com.nextinger.binancer.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nextinger.binancer.client.binance.dto.Order

@Entity(tableName = "order_table")
data class OrderDbo(
    @PrimaryKey
    val orderId: Long,
    val symbol: String,
    val clientOrderId: String,
    val cummulativeQuoteQty: String,
    val executedQty: Float,
    val icebergQty: String,
    val isWorking: Boolean,
    val orderListId: Int,
    val origQty: Float,
    val origQuoteOrderQty: String,
    val price: Float,
    val side: String,
    val status: String,
    val stopPrice: String,
    val time: Long,
    val timeInForce: String,
    val type: String,
    val updateTime: Long
    ) {

    // TODO
    constructor(order: Order) : this(
            order.orderId,
            order.symbol,
            order.clientOrderId,
            order.cummulativeQuoteQty,
            order.executedQty,
            order.icebergQty,
            order.isWorking,
            order.orderListId,
            order.origQty,
            order.origQuoteOrderQty,
            order.price,
            order.side,
            order.status,
            order.stopPrice,
            order.time,
            order.timeInForce,
            order.type,
            order.updateTime
        )
}