package com.nik.weathernik.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nik.weathernik.R

val DMsans = FontFamily(
    Font(R.font.dmsans)   // .ttf jo res/font/ me hai
)

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = DMsans,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = DMsans,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = DMsans,
        fontSize = 12.sp
    ),
    titleLarge = TextStyle(
        fontFamily = DMsans,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = DMsans,
        fontSize = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = DMsans,
        fontSize = 10.sp
    )
)