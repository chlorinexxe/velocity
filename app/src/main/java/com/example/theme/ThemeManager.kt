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
    SLATE("Arctic Slate"),
    CYBERPUNK("Cyberpunk Neon"),
    RACING_GREEN("Racing Green"),
    VOLCANIC_RED("Volcanic Lava"),
    AURORA_BLUE("Aurora Blue"),
    FOREST_NIGHT("Forest Night"),
    PLUM_DARK("Plum Night"),
    OCEAN_DEPTH("Ocean Depth"),
    IVORY_CLOUD("Ivory Cloud"),
    WARM_PAPER("Warm Paper"),
    COOL_MIST("Cool Mist"),
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

    // 8. Cyberpunk Neon (Electric purple & neon magenta)
    val CyberpunkBg = Color(0xFF0E0B25)
    val CyberpunkSurface = Color(0xFF161238)
    val CyberpunkPrimary = Color(0xFF00FFFF) // Cyan
    val CyberpunkOnBg = Color(0xFFFFFFFF)
    val CyberpunkAccent = Color(0xFFFF007F) // Neon Pink

    // 9. Racing Green (Deep track green & golden yellow details)
    val RacingGreenBg = Color(0xFF091611)
    val RacingGreenSurface = Color(0xFF11261E)
    val RacingGreenPrimary = Color(0xFFE5FFFA)
    val RacingGreenOnBg = Color(0xFFF0FFF5)
    val RacingGreenAccent = Color(0xFFFFD700) // Gold

    // 10. Volcanic Lava (Obsidian dark & melting molten magma glow)
    val VolcanicBg = Color(0xFF140F0F)
    val VolcanicSurface = Color(0xFF221616)
    val VolcanicPrimary = Color(0xFFFFFAFA)
    val VolcanicOnBg = Color(0xFFFFFFFE)
    val VolcanicAccent = Color(0xFFFF3B30) // Lava Red

    // 11. Aurora Blue (deep blue tinted dark)
    val AuroraBg = Color(0xFF0B1220)
    val AuroraSurface = Color(0xFF121C2E)
    val AuroraPrimary = Color(0xFFE6F0FF)
    val AuroraOnBg = Color(0xFFEAF2FF)
    val AuroraAccent = Color(0xFF4DA3FF)

    val ForestBg = Color(0xFF0D1512)
    val ForestSurface = Color(0xFF15211C)
    val ForestPrimary = Color(0xFFE6FFF3)
    val ForestOnBg = Color(0xFFEFFCF6)
    val ForestAccent = Color(0xFF2EE59D)

    val PlumBg = Color(0xFF140B18)
    val PlumSurface = Color(0xFF1D1024)
    val PlumPrimary = Color(0xFFF5E9FF)
    val PlumOnBg = Color(0xFFF7EDFF)
    val PlumAccent = Color(0xFFC77DFF)

    val OceanBg = Color(0xFF071A1C)
    val OceanSurface = Color(0xFF0E2A2F)
    val OceanPrimary = Color(0xFFE6FFFC)
    val OceanOnBg = Color(0xFFEFFFFD)
    val OceanAccent = Color(0xFF3DE2C2)

    val IvoryBg = Color(0xFFFAF8F5)
    val IvorySurface = Color(0xFFFFFFFF)
    val IvoryPrimary = Color(0xFF1C1C1E)
    val IvoryOnBg = Color(0xFF2C2C2E)
    val IvoryAccent = Color(0xFF5C6BC0)

    val PaperBg = Color(0xFFF6F1E8)
    val PaperSurface = Color(0xFFFFFFFF)
    val PaperPrimary = Color(0xFF2D2A26)
    val PaperOnBg = Color(0xFF3A352F)
    val PaperAccent = Color(0xFFB08968)

    val MistBg = Color(0xFFF5F7FB)
    val MistSurface = Color(0xFFFFFFFF)
    val MistPrimary = Color(0xFF1F2937)
    val MistOnBg = Color(0xFF374151)
    val MistAccent = Color(0xFF3B82F6)



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
            AppTheme.CYBERPUNK -> darkColorScheme(
                primary = CyberpunkPrimary,
                secondary = CyberpunkAccent,
                background = CyberpunkBg,
                surface = CyberpunkSurface,
                onPrimary = CyberpunkBg,
                onSecondary = CyberpunkPrimary,
                onBackground = CyberpunkOnBg,
                onSurface = CyberpunkOnBg
            )
            AppTheme.RACING_GREEN -> darkColorScheme(
                primary = RacingGreenPrimary,
                secondary = RacingGreenAccent,
                background = RacingGreenBg,
                surface = RacingGreenSurface,
                onPrimary = RacingGreenBg,
                onSecondary = RacingGreenPrimary,
                onBackground = RacingGreenOnBg,
                onSurface = RacingGreenOnBg
            )
            AppTheme.VOLCANIC_RED -> darkColorScheme(
                primary = VolcanicPrimary,
                secondary = VolcanicAccent,
                background = VolcanicBg,
                surface = VolcanicSurface,
                onPrimary = VolcanicBg,
                onSecondary = VolcanicPrimary,
                onBackground = VolcanicOnBg,
                onSurface = VolcanicOnBg
            )
            AppTheme.AURORA_BLUE -> darkColorScheme(
                primary = AuroraPrimary,
                secondary = AuroraAccent,
                background = AuroraBg,
                surface = AuroraSurface,
                onPrimary = AuroraBg,
                onSecondary = AuroraPrimary,
                onBackground = AuroraOnBg,
                onSurface = AuroraOnBg
            )
            AppTheme.FOREST_NIGHT -> darkColorScheme(
                primary = ForestPrimary,
                secondary = ForestAccent,
                background = ForestBg,
                surface = ForestSurface,
                onPrimary = ForestBg,
                onSecondary = ForestPrimary,
                onBackground = ForestOnBg,
                onSurface = ForestOnBg
            )
                AppTheme.PLUM_DARK -> darkColorScheme(
                primary = PlumPrimary,
                secondary = PlumAccent,
                background = PlumBg,
                surface = PlumSurface,
                onPrimary = PlumBg,
                onSecondary = PlumPrimary,
                onBackground = PlumOnBg,
                onSurface = PlumOnBg
            )
            AppTheme.OCEAN_DEPTH -> darkColorScheme(
                primary = OceanPrimary,
                secondary = OceanAccent,
                background = OceanBg,
                surface = OceanSurface,
                onPrimary = OceanBg,
                onSecondary = OceanPrimary,
                onBackground = OceanOnBg,
                onSurface = OceanOnBg
            )

            AppTheme.IVORY_CLOUD -> lightColorScheme(
            primary = IvoryPrimary,
            secondary = IvoryAccent,
            background = IvoryBg,
            surface = IvorySurface,
            onPrimary = IvorySurface,
            onSecondary = IvoryPrimary,
            onBackground = IvoryOnBg,
            onSurface = IvoryOnBg
            )
            AppTheme.WARM_PAPER -> lightColorScheme(
                primary = PaperPrimary,
                secondary = PaperAccent,
                background = PaperBg,
                surface = PaperSurface,
                onPrimary = PaperSurface,
                onSecondary = PaperPrimary,
                onBackground = PaperOnBg,
                onSurface = PaperOnBg
            )

            AppTheme.COOL_MIST -> lightColorScheme(
                primary = MistPrimary,
                secondary = MistAccent,
                background = MistBg,
                surface = MistSurface,
                onPrimary = MistSurface,
                onSecondary = MistPrimary,
                onBackground = MistOnBg,
                onSurface = MistOnBg
            )
        }
    }
}
