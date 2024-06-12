package presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFF5455A9)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFE1DFFF)
val md_theme_light_onPrimaryContainer = Color(0xFF0C0664)
val md_theme_light_secondary = Color(0xFF5D5C72)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFE2E0F9)
val md_theme_light_onSecondaryContainer = Color(0xFF1A1A2C)
val md_theme_light_tertiary = Color(0xFF186C32)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFA2F6AB)
val md_theme_light_onTertiaryContainer = Color(0xFF002109)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF1C1B1F)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF1C1B1F)
val md_theme_light_surfaceVariant = Color(0xFFE4E1EC)
val md_theme_light_onSurfaceVariant = Color(0xFF47464F)
val md_theme_light_outline = Color(0xFF777680)
val md_theme_light_inverseOnSurface = Color(0xFFF3EFF4)
val md_theme_light_inverseSurface = Color(0xFF313034)
val md_theme_light_inversePrimary = Color(0xFFC1C1FF)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF5455A9)
val md_theme_light_outlineVariant = Color(0xFFC8C5D0)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFC1C1FF)
val md_theme_dark_onPrimary = Color(0xFF242478)
val md_theme_dark_primaryContainer = Color(0xFF3C3D8F)
val md_theme_dark_onPrimaryContainer = Color(0xFFE1DFFF)
val md_theme_dark_secondary = Color(0xFFC6C4DD)
val md_theme_dark_onSecondary = Color(0xFF2F2F42)
val md_theme_dark_secondaryContainer = Color(0xFF454559)
val md_theme_dark_onSecondaryContainer = Color(0xFFE2E0F9)
val md_theme_dark_tertiary = Color(0xFF87D991)
val md_theme_dark_onTertiary = Color(0xFF003914)
val md_theme_dark_tertiaryContainer = Color(0xFF005320)
val md_theme_dark_onTertiaryContainer = Color(0xFFA2F6AB)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF1C1B1F)
val md_theme_dark_onBackground = Color(0xFFE5E1E6)
val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_onSurface = Color(0xFFE5E1E6)
val md_theme_dark_surfaceVariant = Color(0xFF47464F)
val md_theme_dark_onSurfaceVariant = Color(0xFFC8C5D0)
val md_theme_dark_outline = Color(0xFF918F9A)
val md_theme_dark_inverseOnSurface = Color(0xFF1C1B1F)
val md_theme_dark_inverseSurface = Color(0xFFE5E1E6)
val md_theme_dark_inversePrimary = Color(0xFF5455A9)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFC1C1FF)
val md_theme_dark_outlineVariant = Color(0xFF47464F)
val md_theme_dark_scrim = Color(0xFF000000)


val seed = Color(0xFFB3B3E9)
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
val md_theme_light_onSuccess = Color(0xFF102000)

val md_theme_dark_success = Color(0xFF354E16)
val md_theme_dark_onSuccess = Color(0xFFCDEDA3)

val md_theme_light_line_chart_color = md_theme_light_primary
val md_theme_light_point_chart_color = Color(250, 114, 2)

val md_theme_dark_line_chart_color = md_theme_dark_primary
val md_theme_dark_point_chart_color = Color(250, 114, 2)

val md_theme_light_axis_line_chart_color = md_theme_light_onBackground.copy(alpha = 0.5f)
val md_theme_dark_axis_line_chart_color = md_theme_dark_onBackground.copy(alpha = 0.5f)

//region::Charts
val md_theme_light_line_color_1 = Color(0xFF865319)
val md_theme_dark_line_color_1 = Color(0xFFfdb876)

val md_theme_light_grid_color = Color(0xFF837469)
val md_theme_dark_grid_color = Color(0xFF9e8e81)
//endregion

@Immutable
data class ExtendedColors(
    val successContainer: Color,
    val onSuccessContainer: Color,

    val lineChartColor1: Color,
    val lineChartGripColor: Color,

    val lineChartAxisColor: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        successContainer = Color.Unspecified,
        onSuccessContainer = Color.Unspecified,
        lineChartColor1 = Color.Unspecified,
        lineChartGripColor = Color.Unspecified,
        lineChartAxisColor = Color.Unspecified
    )
}


internal val lightColors = ColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint
)

internal val darkColors = ColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint
)