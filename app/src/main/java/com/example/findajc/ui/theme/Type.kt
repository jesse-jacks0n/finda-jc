package com.example.findajc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.findajc.R

// Load the Raleway font
val RalewayFontFamily = FontFamily(
    Font(R.font.raleway_light, FontWeight.Light),
    Font(R.font.raleway_regular),
    Font(R.font.raleway_medium, FontWeight.Medium),
    Font(R.font.raleway_bold, FontWeight.Bold)

)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = RalewayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override */
)
