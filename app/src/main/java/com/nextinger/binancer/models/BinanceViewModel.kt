package com.nextinger.binancer.models

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.nextinger.binancer.R
import com.nextinger.binancer.client.binance.dto.*
import com.nextinger.binancer.data.Repository
import com.nextinger.binancer.data.database.entity.KnownSymbol
import com.nextinger.binancer.data.database.entity.OrderDbo
import com.nextinger.binancer.data.objects.NetworkResult
import com.nextinger.binancer.di.ResourcesProvider
import com.nextinger.binancer.ui.ApiCheckSplashActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class BinanceViewModel @Inject constructor(
    private val repository: Repository,
    private val resourcesProvider: ResourcesProvider,
    application: Application
) : AndroidViewModel(application) {

    // REMOTE
    var accountInfoInfoResult: MutableLiveData<NetworkResult<AccountInfo>> = MutableLiveData()
    var allSymbolPriceTickerResult: MutableLiveData<NetworkResult<List<SymbolExchangeRate>>> =
        MutableLiveData()
    var allCoinsInformationResult: MutableLiveData<NetworkResult<List<CoinInfo>>> =
        MutableLiveData()
    var ordersResult: MutableLiveData<NetworkResult<List<Order>>> = MutableLiveData()
    var tradesResult: MutableLiveData<NetworkResult<List<Trade>>> = MutableLiveData()

    // LOCAL
    val knownSymbols: LiveData<List<KnownSymbol>> =
        repository.local.getAllKnownSymbols().asLiveData()
    val storedOrders: LiveData<List<OrderDbo>> = repository.local.getAllOrders().asLiveData()


    // TODO
    // var apiIpWeight: MutableLiveData<Int> = MutableLiveData()
    // private var napTime: Long = 0

    /**
     * LOCAL
     */

    fun getOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.getAllOrders()
        }
    }

    fun insertKnowSymbol(knownSymbol: KnownSymbol) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertKnownSymbol(knownSymbol)
        }
    }

    fun deleteKnowSymbols() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllKnownSymbols()
        }
    }

    /**
     *  REMOTE
     */

    /**
     * Get all available coins information
     */
    fun getAllCoinsInformation() = viewModelScope.launch {
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAllCoinsInformation()
                allCoinsInformationResult.value = handleResponse(response)
            } catch (e: Exception) {
                allCoinsInformationResult.value = NetworkResult.Error(
                    resourcesProvider.getString(R.string.error_something_went_wrong)
                )
            }
        }
    }

    /**
     * Get account info
     */
    fun getAccountInfo() = viewModelScope.launch {
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAccountInfo()
                accountInfoInfoResult.value = handleResponse(response)
            } catch (e: Exception) {
                allCoinsInformationResult.value = NetworkResult.Error(
                    resourcesProvider.getString(R.string.error_something_went_wrong)
                )
            }
        }
    }

    /**
     * Get all symbol prices
     */
    fun getAllSymbolPriceTicker() = viewModelScope.launch {
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAllSymbolPriceTicker()
                allSymbolPriceTickerResult.value = handleResponse(response)
            } catch (e: Exception) {
                allCoinsInformationResult.value = NetworkResult.Error(
                    resourcesProvider.getString(R.string.error_something_went_wrong)
                )
            }
        }
    }

    /**
     * Get orders
     */
    fun getOrders(symbol: String, orderId: Long?, limit: Int?) = viewModelScope.launch {
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getOrders(symbol, orderId, limit)
                ordersResult.value = handleResponse(response)
            } catch (e: Exception) {
                allCoinsInformationResult.value = NetworkResult.Error(
                    resourcesProvider.getString(R.string.error_something_went_wrong)
                )
            }
        }
    }

    /**
     * Get trades
     */
    fun getTrades(symbol: String, orderId: Long?, limit: Int?) = viewModelScope.launch {
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getTrades(symbol, orderId, limit)
                tradesResult.value = handleResponse(response)
            } catch (e: Exception) {
                allCoinsInformationResult.value = NetworkResult.Error(
                    resourcesProvider.getString(R.string.error_something_went_wrong)
                )
            }
        }
    }

    /**
     * Handle response
     */
    private fun <T> handleResponse(response: Response<T>): NetworkResult<T> {

        // x-mbx-used-weight
        // x-mbx-used-weight-1m

        return when {
            response.isSuccessful && response.body() != null -> {
                NetworkResult.Success(response.body())
            }
            response.code() == 400 -> { // bad request
                //real code: -2008 msg Invalid Api-Key ID --- BUT! doc says -2015 REJECTED_MBX_KEY
                // TODO - ONLY QUICK FIX - 400 - in all known cases Inalid API
                viewModelScope.launch(Dispatchers.IO) {
                    // clear current API key data
                    SettingsModel.apiKey.update("")
                    SettingsModel.apiSecret.update("")
                    withContext(Dispatchers.Main) {
                        // restart app
                        val context = getApplication<Application>().applicationContext
                        val intent = Intent(context, ApiCheckSplashActivity::class.java)
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent)
                        Runtime.getRuntime().exit(0)
                    }
                }
                NetworkResult.Error("Invalid API key")
            }
            response.code() == 429 -> { // too many requests - back off
                //takeANap()
                // TODO
                NetworkResult.Error("Stop using app - Too many request")
            }
            response.code() == 418 -> { // well you just get banned
                //takeANap()
                // TODO
                NetworkResult.Error("Too many requests your IP banned")
            }
            else -> {
                NetworkResult.Error("Some error occurred")
            }
        }

    }

    /**
     * Check if any network connection available
     */
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}