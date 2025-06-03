package cat.deim.asm_22.p2_patinfly.presentation.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import cat.deim.asm_22.p2_patinfly.data.datasource.database.AppDatabase
import cat.deim.asm_22.p2_patinfly.data.datasource.local.BikeLocalDataSource
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.BikeRemoteDataSource
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository
import cat.deim.asm_22.p2_patinfly.presentation.ui.theme.PatinflyTheme
import cat.deim.asm_22.p2_patinfly.presentation.components.TopAppBar

/**
 * Activity que muestra la pantalla de perfil del usuario.
 * Inicializa las fuentes de datos y el repositorio necesario para el ViewModel.
 * Configura el contenido Compose con el tema de la app y la UI del perfil.
 */
class ProfileActivity : ComponentActivity() {

    /**
     * Metodo llamado al crear la Activity.
     * Inicializa las fuentes de datos, crea el repositorio, y establece la UI Compose.
     *
     * @param savedInstanceState Bundle con el estado guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita el modo edge-to-edge para que la UI use toda la pantalla, incluyendo zonas de notificación y navegación
        enableEdgeToEdge()

        // Contexto de la aplicación para acceder a recursos y bases de datos
        val context = applicationContext

        // Obtiene la fuente de datos para usuarios desde la base de datos local Room
        val userDataSource = AppDatabase.getDatabase(context).userDataSource()

        // Obtiene la fuente de datos para bicicletas desde la base de datos local Room
        val bikeDatasource = AppDatabase.getDatabase(context).bikeDataSource()

        // Obtiene la fuente de datos remota para bicicletas (API REST)
        val bikeApiDataSource = BikeRemoteDataSource.getInstance()

        // Obtiene la fuente de datos local para bicicletas (almacenamiento local)
        val bikeLocalDataSource = BikeLocalDataSource.getInstance(context)

        // Crea el repositorio de bicicletas que combinará fuentes remotas y locales
        val repository = BikeRepository(
            bikeDatasource = bikeDatasource,
            remoteDataSource = bikeApiDataSource,
            localDataSource = bikeLocalDataSource,
            context = context,
        )

        // Establece la UI usando Jetpack Compose
        setContent {
            // Aplica el tema visual de la aplicación
            PatinflyTheme {
                // Contenedor principal vertical que ocupa toda la pantalla
                Column(modifier = Modifier.fillMaxSize()) {
                    // Barra superior con botón para volver atrás
                    TopAppBar(navigateBack = { finish() })

                    // Pantalla principal del perfil con el ViewModel creado
                    ProfileScreen(
                        viewModel = ProfileViewModel(
                            userDataSource = userDataSource,
                            bikeRepository = repository
                        )
                    )
                }
            }
        }
    }
}
