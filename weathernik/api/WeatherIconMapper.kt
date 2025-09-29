package com.nik.weathernik.api

import com.nik.weathernik.R

fun getWeatherIconRes(code: Int, isDay: Boolean): Int {
    return when (code) {
        // Clear / Sunny
        1000 -> if (isDay) R.drawable.img_sun else R.drawable.img_moon_stars

        // Partly Cloudy
        1003 -> if (isDay) R.drawable.img_clouds else R.drawable.img_rain

        // Cloudy / Overcast
        1006, 1009 -> R.drawable.cloud

        // Mist / Fog
        1030, 1135, 1147 -> R.drawable.img_cloudy

        // Rain variations
        1063, 1150, 1153, 1168, 1171, 1180, 1183,
        1186, 1189, 1192, 1195, 1198, 1201,
        1240, 1243, 1246 -> R.drawable.img_rain

        // Sun + Rain
        1249, 1252 -> R.drawable.img_sub_rain

        //️ Snow
        1066, 1072, 1114, 1117, 1210, 1213,
        1216, 1219, 1222, 1225, 1255, 1258 -> R.drawable.cloudy_snow

        // Thunder
        1087, 1273, 1276, 1279, 1282 -> R.drawable.img_thunder

        else -> R.drawable.img_sun // fallback
    }
}
