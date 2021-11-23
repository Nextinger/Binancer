package com.nextinger.binancer.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nextinger.binancer.data.database.entity.KnownSymbol
import com.nextinger.binancer.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface KnownSymbolDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserKnownSymbol(knownSymbol: KnownSymbol)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserKnownSymbols(knownSymbols: List<KnownSymbol>)

    @Query("SELECT * FROM ${Constants.KNOWN_SYMBOLS_TABLE}")
    fun selectAllKnownSymbols(): LiveData<List<KnownSymbol>>

    @Query("SELECT * FROM ${Constants.KNOWN_SYMBOLS_TABLE}")
    fun selectAllKnownSymbolsFlow(): Flow<List<KnownSymbol>>

    @Query("DELETE FROM ${Constants.KNOWN_SYMBOLS_TABLE}")
    fun deleteAllKnownSymbols()

}