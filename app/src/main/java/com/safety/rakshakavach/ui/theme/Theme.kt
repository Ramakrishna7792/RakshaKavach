package com.safety.rakshakavach.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val IndustrialYellow = Color(0xFFFFD700)
val IndustrialBlack = Color(0xFF222222)
val IndustrialLight = Color(0xFFFFFDF5)
val IndustrialGray = Color(0xFFF5F5F0)

private val LightColorScheme = lightColorScheme(
    primary = IndustrialYellow,
    onPrimary = Color.Black,
    secondary = Color(0xFFFFE680),
    background = IndustrialLight,
    surface = Color.White,
    onSurface = IndustrialBlack,
    onBackground = IndustrialBlack
)

@Composable
fun RakshaKavachTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
