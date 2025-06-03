package cat.deim.asm_22.p2_patinfly.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Definición del esquema de colores para el tema oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// Definición del esquema de colores para el tema claro
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun PatinflyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // La opción dynamicColor está disponible a partir de Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determinamos el esquema de colores según el sistema y la configuración de color dinámico
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Aplicamos el tema con los colores y la tipografía personalizada
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PatinflyTypography, // Usamos la tipografía personalizada
        content = content
    )
}
