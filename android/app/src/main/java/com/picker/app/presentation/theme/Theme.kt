package com.picker.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF1F1F1F),
    secondary = Color(0xFFFFC400),
    background = Color(0xFFFAFAFA),
)

private val Dark = darkColorScheme(
    primary = Color(0xFFEFEFEF),
    secondary = Color(0xFFFFC400),
    background = Color(0xFF121212),
)

@Composable
fun PickerTheme(useDark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (useDark) Dark else Light, content = content)
}
