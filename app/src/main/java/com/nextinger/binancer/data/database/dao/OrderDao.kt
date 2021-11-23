package com.nextinger.binancer.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nextinger.binancer.data.database.entity.OrderDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrder(order: OrderDbo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrders(orders: List<OrderDbo>)

    @Query("SELECT * FROM order_table")
    fun selectAllOrders(): LiveData<List<OrderDbo>>

    @Query("SELECT * FROM order_table")
    fun selectAllOrdersFlow(): Flow<List<OrderDbo>>

}