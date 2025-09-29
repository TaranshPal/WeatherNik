package com.nik.weathernik.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/v1/current.json")
    suspend fun getWeather(
        @Query("key") apikey : String = Constant.apiKey,
        @Query("q") city : String,
    ) : Response<WeatherModel>
}