package com.example.frontend.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WarehouseGreenBright,
    secondary = WarehouseGreen,
    tertiary = WarehouseMuted,
    background = WarehouseBackground,
    surface = WarehouseSurface,
    onPrimary = WarehouseBackground,
    onSecondary = WarehouseText,
    onBackground = WarehouseText,
    onSurface = WarehouseText
)

private val LightColorScheme = lightColorScheme(
    primary = WarehouseGreen,
    secondary = WarehouseGreenBright,
    tertiary = WarehouseMuted,
    background = WarehouseText,
    surface = Color(0xFFE9EEE9),
    onPrimary = Color.White,
    onSecondary = WarehouseBackground,
    onBackground = WarehouseBackground,
    onSurface = WarehouseBackground

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun FRONTENDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
        typography = Typography,
        content = content
    )
}
