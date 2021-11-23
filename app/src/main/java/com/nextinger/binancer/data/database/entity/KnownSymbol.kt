package com.nextinger.binancer.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nextinger.binancer.utils.Constants

@Entity(tableName = Constants.KNOWN_SYMBOLS_TABLE)
data class KnownSymbol(
    @PrimaryKey
    val symbol: String,
    val name: String?
)