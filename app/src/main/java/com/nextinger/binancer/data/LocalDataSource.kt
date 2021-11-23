package com.nextinger.binancer.data

import com.nextinger.binancer.data.database.dao.KnownSymbolDao
import com.nextinger.binancer.data.database.dao.OrderDao
import com.nextinger.binancer.data.database.entity.KnownSymbol
import com.nextinger.binancer.data.database.entity.OrderDbo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
        private val orderDao: OrderDao,
        private val knownSymbolDao: KnownSymbolDao
    ) {

    fun getAllOrders(): Flow<List<OrderDbo>> {
        return orderDao.selectAllOrdersFlow()
    }

    suspend fun insertOrder(order: OrderDbo) {
        orderDao.insertOrder(order)
    }

    suspend fun insertOrders(orders: List<OrderDbo>) {
        orderDao.insertOrders(orders)
    }

    fun getAllKnownSymbols(): Flow<List<KnownSymbol>> {
        return knownSymbolDao.selectAllKnownSymbolsFlow()
    }

    suspend fun insertKnownSymbol(knownSymbol: KnownSymbol) {
        knownSymbolDao.inserKnownSymbol(knownSymbol)
    }

    suspend fun insertKnownSymbols(knownSymbols: List<KnownSymbol>) {
        knownSymbolDao.inserKnownSymbols(knownSymbols)
    }

    fun deleteAllKnownSymbols() {
        knownSymbolDao.deleteAllKnownSymbols()
    }
}