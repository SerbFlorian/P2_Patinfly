package cat.deim.asm_22.p2_patinfly.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.MaterialTheme
import cat.deim.asm_22.p2_patinfly.R
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.deim.asm_22.p2_patinfly.presentation.login.LoginActivity
import cat.deim.asm_22.p2_patinfly.presentation.ui.theme.PatinflyTheme

/**
 * Actividad inicial (Splash Screen) que se muestra al abrir la aplicación.
 *
 * Esta pantalla cumple varios propósitos clave:
 * 1. Mostrar la identidad visual de la marca (logo, colores, tipografía)
 * 2. Realizar inicializaciones críticas mientras el usuario ve contenido
 * 3. Redirigir al flujo de autenticación (LoginActivity)
 *
 * Tiempo de visualización: Inmediato hasta que se completa la carga inicial
 * Transición: Automática a LoginActivity al presionar el botón
 */
@SuppressLint("CustomSplashScreen") // Suprime advertencia por no usar SplashScreen API de Android 12+
class SplashActivity : ComponentActivity() {
    /**
     * Punto de entrada principal para la configuración de la actividad.
     *
     * Metodo clave que:
     * - Configura el diseño usando Jetpack Compose
     * - Establece el tema visual de la aplicación
     * - Maneja la navegación al presionar el botón
     *
     * @param savedInstanceState Bundle que contiene el estado previo (null en primera ejecución)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración del contenido usando Jetpack Compose
        setContent {
            PatinflyTheme { // Aplica el tema personalizado de la app
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Contenido principal del splash
                    SplashContent {
                        // Navegación a LoginActivity al presionar el botón
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish() // Cierra esta actividad para no volver atrás
                    }
                }
            }
        }
    }
}

/**
 * Componente Composable que define la interfaz visual del Splash Screen.
 *
 * Estructura visual:
 * 1. Divisores decorativos
 * 2. Logo de la aplicación (texto grande)
 * 3. Eslogan descriptivo
 * 4. Imagen ilustrativa central
 * 5. Botón de acción principal
 *
 * @param onButtonClick Callback que se ejecuta al presionar el botón "Get Started"
 *                      (Normalmente para navegar a la siguiente pantalla)
 */
@Composable
fun SplashContent(onButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Centrado horizontal
        verticalArrangement = Arrangement.Center // Centrado vertical
    ) {
        // --- SECCIÓN SUPERIOR ---
        // Divisor decorativo
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            thickness = 1.dp
        )

        // Logo textual de la aplicación
        // NOTA: En una versión profesional, sería una imagen SVG/PNG
        Text(
            text = "Patinfly",
            fontSize = 60.sp, // Tamaño muy grande para impacto visual
            fontWeight = FontWeight.Bold, // Negrita para mejor legibilidad
            modifier = Modifier.padding(bottom = 8.dp),
        )

        // Divisor decorativo inferior
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            thickness = 1.dp
        )

        // Eslogan descriptivo
        Text(
            text = "Explore urban areas on two wheels",
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), // Texto semitransparente
            modifier = Modifier.padding(top = 30.dp, bottom = 30.dp)
        )

        // --- IMAGEN CENTRAL ---
        // Imagen principal (debe estar en res/drawable)
        // IMPORTANTE: Usar una imagen optimizada para diferentes densidades de pantalla
        Image(
            painter = painterResource(id = R.drawable.splash_image),
            contentDescription = "Splash Image", // Texto para accesibilidad
            modifier = Modifier
                .size(400.dp) // Tamaño fijo para consistencia visual
                .padding(bottom = 16.dp)
        )

        // --- BOTÓN PRINCIPAL ---
        // Elemento interactivo clave
        Button(
            onClick = onButtonClick, // Maneja el evento de clic
            modifier = Modifier
                .width(400.dp) // Ancho fijo para consistencia
                .padding(top = 50.dp), // Espaciado superior grande
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff5fff33) // Verde brillante (cambiar por color del tema)
            )
        ) {
            Text(
                text = "Get Started", // Texto del botón
                color = Color.Black, // Contraste con fondo claro
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}