package com.nextinger.binancer.di

import android.content.Context
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.nextinger.binancer.client.binance.BinanceApi
import com.nextinger.binancer.client.binance.security.AuthenticationInterceptor
import com.nextinger.binancer.models.SettingsModel
import com.nextinger.binancer.utils.Constants
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

        return Retrofit.Builder()
            .baseUrl(Constants.Companion.BINANCE_BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpClient(@ApplicationContext appContext: Context): OkHttpClient {
        // blocking datastore
        val apiKey = SettingsModel.apiKey.value
        val apiSecret = SettingsModel.apiSecret.value
        val authenticationInterceptor = AuthenticationInterceptor(apiKey, apiSecret)

        return OkHttpClient.Builder()
            .addInterceptor(authenticationInterceptor)
            .addInterceptor(ChuckInterceptor(appContext))
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideBinanceService(retrofit: Retrofit): BinanceApi {
        return retrofit.create(BinanceApi::class.java)
    }

}