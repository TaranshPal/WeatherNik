package com.nik.weathernik

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.nik.weathernik.ui.theme.WeatherNikTheme

class MainActivity : ComponentActivity() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var foregroundPermissionLauncher: ActivityResultLauncher<Array<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        // Register permission launcher
        foregroundPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            val granted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    results[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                // Permission granted -> fetch location-based weather
                weatherViewModel.fetchWeatherByCurrentLocation(this)
                startWeatherService()            } else {
                // Permission denied
                val askedBefore = PermissionUtils.hasAskedForeground(this)
                val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                )

                if (!showRationale && askedBefore) {
                    // Permanently denied
                    showOpenSettingsDialog()
                } else {
                    showRationaleDialog()
                }
            }
        }

        // Request permission at startup
        if (PermissionUtils.hasForegroundLocation(this)) {
            weatherViewModel.fetchWeatherByCurrentLocation(this)
            startWeatherService()
        } else {
            PermissionUtils.requestForegroundIfNeeded(this, foregroundPermissionLauncher)
        }

        enableEdgeToEdge()
        setContent {
            WeatherNikTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                    ) {
                    WeatherPage(weatherViewModel)
                }
            }
        }

    }

    private fun startWeatherService() {
        val intent = Intent(this, WeatherNotificationService::class.java)
        startForegroundService(intent)
    }

    private fun showOpenSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Permission permanently denied. Please enable location permissions in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                PermissionUtils.openAppSettings(this)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Needed")
            .setMessage("We need location access to show local weather data.")
            .setPositiveButton("Allow") { _, _ ->
                PermissionUtils.requestForegroundIfNeeded(this, foregroundPermissionLauncher)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}