package com.nik.weathernik

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionUtils {

    private const val PREFS_NAME = "perm_prefs"
    private const val KEY_ASKED_FOREGROUND = "asked_foreground"

    // Foreground permissions
    val FOREGROUND_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // if foreground permissions granted
    fun hasForegroundLocation(context: Context): Boolean {
        return FOREGROUND_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Launch foreground permission request
    fun requestForegroundIfNeeded(activity: Activity, launcher: ActivityResultLauncher<Array<String>>) {
        if (!hasForegroundLocation(activity)) {
            markForegroundAsked(activity)
            launcher.launch(FOREGROUND_PERMISSIONS)
        }
    }

    // Open app settings when permanently denied
    fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }

    private fun markForegroundAsked(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ASKED_FOREGROUND, true).apply()
    }

    fun hasAskedForeground(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ASKED_FOREGROUND, false)
    }
}
