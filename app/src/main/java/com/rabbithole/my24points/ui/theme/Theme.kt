package com.rabbithole.my24points.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GamePrimaryDark,
    onPrimary = GameOnPrimaryDark,
    primaryContainer = GamePrimaryContainerDark,
    onPrimaryContainer = GameOnPrimaryContainerDark,
    secondary = GameSecondaryDark,
    onSecondary = GameOnSecondaryDark,
    secondaryContainer = GameSecondaryContainerDark,
    onSecondaryContainer = GameOnSecondaryContainerDark,
    tertiary = GameTertiaryDark,
    onTertiary = GameOnTertiaryDark,
    tertiaryContainer = GameTertiaryContainerDark,
    onTertiaryContainer = GameOnTertiaryContainerDark,
    error = GameErrorDark,
    errorContainer = GameErrorContainerDark,
    onError = GameOnErrorDark,
    onErrorContainer = GameOnErrorContainerDark,
    background = GameBackgroundDark,
    onBackground = GameOnBackgroundDark,
    surface = GameSurfaceDark,
    onSurface = GameOnSurfaceDark,
    surfaceVariant = GameSurfaceVariantDark,
    onSurfaceVariant = GameOnSurfaceVariantDark,
    outline = GameOutlineDark,
    outlineVariant = GameOutlineVariantDark,
    inverseSurface = GameInverseSurfaceDark,
    inverseOnSurface = GameInverseOnSurfaceDark,
    scrim = GameScrimDark
)

private val LightColorScheme = lightColorScheme(
    primary = GamePrimary,
    onPrimary = GameOnPrimary,
    primaryContainer = GamePrimaryContainer,
    onPrimaryContainer = GameOnPrimaryContainer,
    secondary = GameSecondary,
    onSecondary = GameOnSecondary,
    secondaryContainer = GameSecondaryContainer,
    onSecondaryContainer = GameOnSecondaryContainer,
    tertiary = GameTertiary,
    onTertiary = GameOnTertiary,
    tertiaryContainer = GameTertiaryContainer,
    onTertiaryContainer = GameOnTertiaryContainer,
    error = GameError,
    errorContainer = GameErrorContainer,
    onError = GameOnError,
    onErrorContainer = GameOnErrorContainer,
    background = GameBackground,
    onBackground = GameOnBackground,
    surface = GameSurface,
    onSurface = GameOnSurface,
    surfaceVariant = GameSurfaceVariant,
    onSurfaceVariant = GameOnSurfaceVariant,
    outline = GameOutline,
    outlineVariant = GameOutlineVariant,
    inverseSurface = GameInverseSurface,
    inverseOnSurface = GameInverseOnSurface,
    scrim = GameScrim
)

@Composable
fun GameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // 禁用动态颜色以使用自定义配色
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
