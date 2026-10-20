package theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Синяя палитра Material 3 (seed #3D6FB6)
val md_theme_light_primary = Color(0xFF2F5FA0)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFD6E3FF)
val md_theme_light_onPrimaryContainer = Color(0xFF001B3E)
val md_theme_light_secondary = Color(0xFF555F71)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFDAE2F9)
val md_theme_light_onSecondaryContainer = Color(0xFF131C2B)
val md_theme_light_tertiary = Color(0xFF006A6A)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFF9CF1F0)
val md_theme_light_onTertiaryContainer = Color(0xFF002020)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFF7F9FF)
val md_theme_light_onBackground = Color(0xFF191C20)
val md_theme_light_surface = Color(0xFFF7F9FF)
val md_theme_light_onSurface = Color(0xFF191C20)
val md_theme_light_surfaceVariant = Color(0xFFDFE2EC)
val md_theme_light_onSurfaceVariant = Color(0xFF43474E)
val md_theme_light_outline = Color(0xFF73777F)
val md_theme_light_inverseOnSurface = Color(0xFFEFF0F7)
val md_theme_light_inverseSurface = Color(0xFF2E3036)
val md_theme_light_inversePrimary = Color(0xFFA9C7FF)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF2F5FA0)
val md_theme_light_outlineVariant = Color(0xFFC3C6CF)
val md_theme_light_scrim = Color(0xFF000000)
val md_theme_light_surfaceDim = Color(0xFFD7DAE2)
val md_theme_light_surfaceBright = Color(0xFFF7F9FF)
val md_theme_light_surfaceContainerLowest = Color(0xFFFFFFFF)
val md_theme_light_surfaceContainerLow = Color(0xFFF1F4FB)
val md_theme_light_surfaceContainer = Color(0xFFEBEEF6)
val md_theme_light_surfaceContainerHigh = Color(0xFFE5E8F0)
val md_theme_light_surfaceContainerHighest = Color(0xFFDFE2EA)

val md_theme_dark_primary = Color(0xFFA9C7FF)
val md_theme_dark_onPrimary = Color(0xFF07305F)
val md_theme_dark_primaryContainer = Color(0xFF1F4780)
val md_theme_dark_onPrimaryContainer = Color(0xFFD6E3FF)
val md_theme_dark_secondary = Color(0xFFBDC7DC)
val md_theme_dark_onSecondary = Color(0xFF273141)
val md_theme_dark_secondaryContainer = Color(0xFF3E4759)
val md_theme_dark_onSecondaryContainer = Color(0xFFDAE2F9)
val md_theme_dark_tertiary = Color(0xFF80D5D4)
val md_theme_dark_onTertiary = Color(0xFF003737)
val md_theme_dark_tertiaryContainer = Color(0xFF004F4F)
val md_theme_dark_onTertiaryContainer = Color(0xFF9CF1F0)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF10141B)
val md_theme_dark_onBackground = Color(0xFFE0E2EA)
val md_theme_dark_surface = Color(0xFF10141B)
val md_theme_dark_onSurface = Color(0xFFE0E2EA)
val md_theme_dark_surfaceVariant = Color(0xFF43474E)
val md_theme_dark_onSurfaceVariant = Color(0xFFC3C6CF)
val md_theme_dark_outline = Color(0xFF8D9199)
val md_theme_dark_inverseOnSurface = Color(0xFF2E3036)
val md_theme_dark_inverseSurface = Color(0xFFE0E2EA)
val md_theme_dark_inversePrimary = Color(0xFF2F5FA0)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFA9C7FF)
val md_theme_dark_outlineVariant = Color(0xFF43474E)
val md_theme_dark_scrim = Color(0xFF000000)
val md_theme_dark_surfaceDim = Color(0xFF10141B)
val md_theme_dark_surfaceBright = Color(0xFF363A42)
val md_theme_dark_surfaceContainerLowest = Color(0xFF0B0F15)
val md_theme_dark_surfaceContainerLow = Color(0xFF181C23)
val md_theme_dark_surfaceContainer = Color(0xFF1C2027)
val md_theme_dark_surfaceContainerHigh = Color(0xFF272A32)
val md_theme_dark_surfaceContainerHighest = Color(0xFF32353D)

val seed = Color(0xFF3D6FB6)
val test = Color(0xFFFBA700)
val light_test = Color(0xFF825500)
val light_ontest = Color(0xFFFFFFFF)
val light_testContainer = Color(0xFFFFDDB4)
val light_ontestContainer = Color(0xFF291800)
val dark_test = Color(0xFFFFB953)
val dark_ontest = Color(0xFF452B00)
val dark_testContainer = Color(0xFF633F00)
val dark_ontestContainer = Color(0xFFFFDDB4)

val md_theme_light_success = Color(0xFFCDEDA3)
val md_theme_light_onSuccess = Color(0xFF5aad07)

val md_theme_dark_success = Color(0xFF354E16)
val md_theme_dark_onSuccess = Color(0xFFCDEDA3)

val md_theme_light_line_chart_color = md_theme_light_primary
val md_theme_light_point_chart_color = Color(250, 114, 2)

val md_theme_dark_line_chart_color = md_theme_dark_primary
val md_theme_dark_point_chart_color = Color(250, 114, 2)

val md_theme_light_axis_line_chart_color = md_theme_light_onBackground.copy(alpha = 0.5f)
val md_theme_dark_axis_line_chart_color = md_theme_dark_onBackground.copy(alpha = 0.5f)

//region::Charts
val md_theme_light_line_color_1 = Color(0xFFed9632)
val md_theme_dark_line_color_1 = Color(0xFFfdb876)

val md_theme_light_line_color_2 = Color(0xFF732fc2)
val md_theme_dark_line_color_2 = Color(0xFFaa78e3)

val md_theme_light_grid_color = Color(0xFF73777F)
val md_theme_dark_grid_color = Color(0xFF8D9199)
//endregion

//region::Constant colors
val trafficLightColor = Color(0xFF2F4859)
val trafficLightBranchColor = Color(0xFF293B44)

object RoadColor {
    val roadSide = Color(0xFFb0b8c1)
}
//endregion

@Immutable
data class ExtendedColors(
    val successContainer: Color,
    val onSuccessContainer: Color,

    val lineChartColor1: Color,
    val lineChartColor2: Color,
    val lineChartGripColor: Color,

    val lineChartAxisColor: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        successContainer = Color.Unspecified,
        onSuccessContainer = Color.Unspecified,
        lineChartColor1 = Color.Unspecified,
        lineChartColor2 = Color.Unspecified,
        lineChartGripColor = Color.Unspecified,
        lineChartAxisColor = Color.Unspecified
    )
}


internal val lightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    inversePrimary = md_theme_light_inversePrimary,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    surfaceTint = md_theme_light_surfaceTint,
    inverseSurface = md_theme_light_inverseSurface,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    outline = md_theme_light_outline,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
    surfaceBright = md_theme_light_surfaceBright,
    surfaceContainer = md_theme_light_surfaceContainer,
    surfaceContainerHigh = md_theme_light_surfaceContainerHigh,
    surfaceContainerHighest = md_theme_light_surfaceContainerHighest,
    surfaceContainerLow = md_theme_light_surfaceContainerLow,
    surfaceContainerLowest = md_theme_light_surfaceContainerLowest,
    surfaceDim = md_theme_light_surfaceDim
)

internal val darkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    inversePrimary = md_theme_dark_inversePrimary,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    surfaceTint = md_theme_dark_surfaceTint,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    outline = md_theme_dark_outline,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
    surfaceBright = md_theme_dark_surfaceBright,
    surfaceContainer = md_theme_dark_surfaceContainer,
    surfaceContainerHigh = md_theme_dark_surfaceContainerHigh,
    surfaceContainerHighest = md_theme_dark_surfaceContainerHighest,
    surfaceContainerLow = md_theme_dark_surfaceContainerLow,
    surfaceContainerLowest = md_theme_dark_surfaceContainerLowest,
    surfaceDim = md_theme_dark_surfaceDim
)
