package cat.deim.asm_22.p2_patinfly.presentation.components

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.deim.asm_22.p2_patinfly.presentation.MainActivity

/**
 * Barra de aplicación superior personalizada con funcionalidades de navegación.
 *
 * Este componente incluye:
 *
 * Botón de retroceso que redirige a [MainActivity]
 * Título centrado de la aplicación
 * Botón de acceso al mapa
 *
 * Comportamiento de navegación:
 *
 * Al presionar el botón de retroceso, limpia el stack de actividades
 * Mantiene el estilo visual consistente con el tema de la aplicación
 *
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navigateBack: () -> Unit) {

    /**
     * Implementación de la TopAppBar de Material3 con elementos personalizados:
     * - Botón izquierdo: Navegación a MainActivity
     * - Título centrado: Nombre de la aplicación
     * - Botón derecho: Acceso al mapa (pendiente de implementación)
     */
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                /**
                 * Botón de navegación hacia atrás
                 *
                 * Configuración especial de flags para:
                 * - [Intent.FLAG_ACTIVITY_CLEAR_TOP]: Limpia el stack de actividades
                 * - [Intent.FLAG_ACTIVITY_SINGLE_TOP]: Evita duplicar la actividad
                 */
                IconButton(
                    onClick = navigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver a la pantalla principal",
                        tint = Color.Black
                    )
                }

                /**
                 * Título de la aplicación centrado
                 *
                 * Usa Modifier.weight para mantener el centrado real
                 * entre los dos botones de acción
                 */
                Text(
                    style = MaterialTheme.typography.displayLarge,
                    text = "Patinfly",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                /**
                 * Contenedor del botón de mapa
                 *
                 * Surface se usa para crear un efecto de "botón flotante"
                 * con sombra y forma circular
                 */
                Box(
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        color = Color.White
                    ) {
                        IconButton(
                            onClick = { /* Implementar navegación al mapa */ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Map,
                                contentDescription = "Abrir mapa de bicicletas",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        ),
        modifier = Modifier.shadow(4.dp) // Sombra sutil para efecto de elevación
    )
}