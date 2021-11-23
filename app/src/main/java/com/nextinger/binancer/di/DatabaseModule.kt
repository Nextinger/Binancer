package com.nextinger.binancer.di

import android.content.Context
import androidx.room.Room
import com.nextinger.binancer.data.database.BinanceDatabase
import com.nextinger.binancer.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        BinanceDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()


    @Provides
    @Singleton
    fun provideOrderDao(database: BinanceDatabase) = database.orderDao()


    @Provides
    @Singleton
    fun provideKnownSymbolDao(database: BinanceDatabase) = database.knownSymbolDao()
}