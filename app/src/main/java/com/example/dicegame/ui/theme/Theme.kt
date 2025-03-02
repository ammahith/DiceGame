package com.example.dicegame.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),      // Light Blue
    secondary = Color(0xFF81C784),    // Light Green
    tertiary = Color(0xFFFFA726),     // Orange
    background = Color(0xFF121212),   // Dark Gray
    surface = Color(0xFF1E1E1E),      // Slightly lighter Gray
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),      // Deep Blue
    secondary = Color(0xFF66BB6A),    // Green
    tertiary = Color(0xFFFFB74D),     // Light Orange
    background = Color(0xFFF5F5F5),   // Light Gray
    surface = Color(0xFFFFFFFF),      // White
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)


val ModernTypography = Typography(
    titleLarge = Typography().titleLarge.copy(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1976D2), // Deep Blue for Titles
        letterSpacing = 0.8.sp
    ),
    bodyMedium = Typography().bodyMedium.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = Typography().bodySmall.copy(
        fontSize = 14.sp,
        letterSpacing = 0.4.sp,
        color = Color.Gray
    ),
    headlineLarge = Typography().headlineLarge.copy(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64B5F6), // Light Blue for Highlights
        letterSpacing = 0.6.sp
    )
)

@Composable
fun DiceGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ModernTypography, // Use Modern Typography
        content = content
    )
}
