package com.example.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String) {
    SOPHISTICATED_DARK("Sophisticated Dark"),
    LIGHT("Classic Light"),
    DARK("Space Dark"),
    OLED("OLED Pure"),
    TITANIUM("Raw Titanium"),
    SAND("Sahara Sand"),
    SLATE("Arctic Slate")
}

object ThemeColors {
    // 1. Classic Light
    val LightBg = Color(0xFFF9F9FA)
    val LightSurface = Color(0xFFFFFFFF)
    val LightPrimary = Color(0xFF111111)
    val LightOnBg = Color(0xFF111111)
    val LightAccent = Color(0xFF007AFF)

    // 2. Space Dark
    val DarkBg = Color(0xFF0D0E11)
    val DarkSurface = Color(0xFF17181D)
    val DarkPrimary = Color(0xFFE2E4E9)
    val DarkOnBg = Color(0xFFF3F4F6)
    val DarkAccent = Color(0xFF30D5C8) // turquoise

    // 3. OLED Pure (100% black)
    val OledBg = Color(0xFF000000)
    val OledSurface = Color(0xFF0A0A0A)
    val OledPrimary = Color(0xFFFFFFFF)
    val OledOnBg = Color(0xFFFFFFFF)
    val OledAccent = Color(0xFFFFCC00) // gold

    // 4. Raw Titanium (warm metallic look, Garmin/Apple Watch inspired)
    val TitaniumBg = Color(0xFF222224)
    val TitaniumSurface = Color(0xFF2C2D30)
    val TitaniumPrimary = Color(0xFFF0F1F4)
    val TitaniumOnBg = Color(0xFFF5F5FA)
    val TitaniumAccent = Color(0xFFFF5200) // Action Orange

    // 5. Sahara Sand (Warm, sophisticated organic)
    val SandBg = Color(0xFFECE6DD)
    val SandSurface = Color(0xFFF4EDE4)
    val SandPrimary = Color(0xFF2B2621)
    val SandOnBg = Color(0xFF3D362F)
    val SandAccent = Color(0xFF8B5E3C) // Warm chocolate/bronze

    // 6. Arctic Slate (Stormy deep steel-blue)
    val SlateBg = Color(0xFF1E2530)
    val SlateSurface = Color(0xFF27303F)
    val SlatePrimary = Color(0xFFE6EFFD)
    val SlateOnBg = Color(0xFFF0F6FF)
    val SlateAccent = Color(0xFF5EADFF) // Ice Blue

    // 7. Sophisticated Dark (OLED Pure, Emerald high contrast details)
    val SophisticatedDarkBg = Color(0xFF000000)
    val SophisticatedDarkSurface = Color(0xFF111111)
    val SophisticatedDarkPrimary = Color(0xFFFFFFFF)
    val SophisticatedDarkOnBg = Color(0xFFFFFFFF)
    val SophisticatedDarkAccent = Color(0xFF10B981) // Emerald accent for dynamic details

    fun getColorScheme(theme: AppTheme): ColorScheme {
        return when (theme) {
            AppTheme.SOPHISTICATED_DARK -> darkColorScheme(
                primary = SophisticatedDarkPrimary,
                secondary = SophisticatedDarkAccent,
                background = SophisticatedDarkBg,
                surface = SophisticatedDarkSurface,
                onPrimary = SophisticatedDarkBg,
                onSecondary = SophisticatedDarkPrimary,
                onBackground = SophisticatedDarkOnBg,
                onSurface = SophisticatedDarkOnBg
            )
            AppTheme.LIGHT -> lightColorScheme(
                primary = LightPrimary,
                secondary = LightAccent,
                background = LightBg,
                surface = LightSurface,
                onPrimary = LightSurface,
                onSecondary = LightPrimary,
                onBackground = LightOnBg,
                onSurface = LightOnBg
            )
            AppTheme.DARK -> darkColorScheme(
                primary = DarkPrimary,
                secondary = DarkAccent,
                background = DarkBg,
                surface = DarkSurface,
                onPrimary = DarkBg,
                onSecondary = DarkPrimary,
                onBackground = DarkOnBg,
                onSurface = DarkOnBg
            )
            AppTheme.OLED -> darkColorScheme(
                primary = OledPrimary,
                secondary = OledAccent,
                background = OledBg,
                surface = OledSurface,
                onPrimary = OledBg,
                onSecondary = OledPrimary,
                onBackground = OledOnBg,
                onSurface = OledOnBg
            )
            AppTheme.TITANIUM -> darkColorScheme(
                primary = TitaniumPrimary,
                secondary = TitaniumAccent,
                background = TitaniumBg,
                surface = TitaniumSurface,
                onPrimary = TitaniumBg,
                onSecondary = TitaniumPrimary,
                onBackground = TitaniumOnBg,
                onSurface = TitaniumOnBg
            )
            AppTheme.SAND -> lightColorScheme(
                primary = SandPrimary,
                secondary = SandAccent,
                background = SandBg,
                surface = SandSurface,
                onPrimary = SandSurface,
                onSecondary = SandPrimary,
                onBackground = SandOnBg,
                onSurface = SandOnBg
            )
            AppTheme.SLATE -> darkColorScheme(
                primary = SlatePrimary,
                secondary = SlateAccent,
                background = SlateBg,
                surface = SlateSurface,
                onPrimary = SlateBg,
                onSecondary = SlatePrimary,
                onBackground = SlateOnBg,
                onSurface = SlateOnBg
            )
        }
    }
}
