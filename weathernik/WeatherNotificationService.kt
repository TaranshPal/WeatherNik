package com.nik.weathernik

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class WeatherNotificationService : Service() {

    private val channelId = "weather_channel_id"
    private val notificationId = 101
    private var job: Job? = null

    private lateinit var weatherApi: WeatherApi
    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initWeatherApi()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                fetchAndUpdateWeather()
                delay(5 * 60 * 1000) // 5 minutes
            }
        }

        startForeground(notificationId, buildNotification("Fetching weather..."))
        return START_STICKY
    }


    @SuppressLint("MissingPermission")
    private fun fetchAndUpdateWeather() {
        if (PermissionUtils.hasForegroundLocation(this)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    CoroutineScope(Dispatchers.IO).launch {
                        callWeatherApi(lat, lon)
                    }
                } else {
                    updateNotification("Location unavailable")
                }
            }.addOnFailureListener {
                updateNotification("Failed to get location")
            }
        } else {
            updateNotification("Permission not granted")
        }
    }


    private suspend fun callWeatherApi(lat: Double, lon: Double) {
        try {
            val apiKey = "b5f8404d6f7a49879ef130657252708" // Replace with your API key
            val response = weatherApi.getWeather(lat, lon, apiKey)
            if (response.isSuccessful && response.body() != null) {
                val weather = response.body()!!
                val text = "${weather.current.temp_c}°C | ${weather.current.condition.text}"
                updateNotification(text)
            } else {
                updateNotification("Unable to fetch weather")
            }
        } catch (e: Exception) {
            updateNotification("Error: ${e.message}")
        }
    }

    private fun updateNotification(content: String) {
        val notification = buildNotification(content)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    private fun buildNotification(content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Current Weather")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_wind) // Replace with your app icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Updates",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun initWeatherApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/") // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

// Retrofit interface
interface WeatherApi {
    @GET("v1/current.json")
    suspend fun getWeather(
        @Query("q") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String
    ): retrofit2.Response<WeatherResponse>
}

// Data classes
data class WeatherResponse(val current: Current)
data class Current(val temp_c: Float, val condition: Condition)
data class Condition(val text: String, val icon: String)
