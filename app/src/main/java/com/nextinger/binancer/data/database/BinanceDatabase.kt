package com.nextinger.binancer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nextinger.binancer.data.database.dao.KnownSymbolDao
import com.nextinger.binancer.data.database.dao.OrderDao
import com.nextinger.binancer.data.database.entity.KnownSymbol
import com.nextinger.binancer.data.database.entity.OrderDbo

@Database(entities = [OrderDbo::class,KnownSymbol::class], version = 1, exportSchema = false)
abstract class BinanceDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun knownSymbolDao(): KnownSymbolDao

}