package com.nik.weathernik

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nik.weathernik.api.Constant
import com.nik.weathernik.api.NetworkResponse
import com.nik.weathernik.api.RetrofitInstance
import com.nik.weathernik.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel :ViewModel() {


    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult : LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city : String){
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey,city)
                if (response.isSuccessful){
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                }
                else{
                    _weatherResult.value = NetworkResponse.Error("Failed To Load Data")
                }
            }
            catch (e : Exception){
                _weatherResult.value = NetworkResponse.Error("Failed To Load Data")
            }
        }
    }

    fun fetchWeatherByCurrentLocation(context: Context){
        viewModelScope.launch {
            try {
                val locationHelper = CurrentLocationWeather(context)
                val location = locationHelper.getCurrentLocation()

                location?.let {
                    val query = "${it.latitude},${it.longitude}"
                    val response = weatherApi.getWeather(Constant.apiKey, query)

                    if (response.isSuccessful) {
                        response.body()?.let {
                            _weatherResult.value = NetworkResponse.Success(it)
                        }
                    } else {
                        _weatherResult.value = NetworkResponse.Error("Failed To Load Data")
                    }
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Failed To Load Data")
            }
        }
    }
}
