package cat.deim.asm_22.p2_patinfly.presentation.detailBike

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import cat.deim.asm_22.p2_patinfly.data.datasource.database.AppDatabase
import cat.deim.asm_22.p2_patinfly.data.datasource.local.BikeLocalDataSource
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.BikeRemoteDataSource
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository
import cat.deim.asm_22.p2_patinfly.presentation.components.TopAppBar
import cat.deim.asm_22.p2_patinfly.presentation.ui.theme.PatinflyTheme

/**
 * Activity que muestra los detalles de una bicicleta específica.
 *
 * Obtiene el ID de la bicicleta del Intent que inicia esta actividad,
 * carga los datos asociados mediante un ViewModel y renderiza la UI
 * usando Jetpack Compose.
 */
class DetailBikeActivity : ComponentActivity() {

    /** ID de la bicicleta que se va a mostrar */
    private lateinit var bikeId: String

    /** ViewModel que gestiona la lógica y datos para los detalles de la bicicleta */
    private lateinit var viewModel: DetailBikeViewModel

    /**
     * Metodo de ciclo de vida onCreate que se llama al crear la Activity.
     *
     * Realiza la inicialización de dependencias, obtiene el bikeId,
     * crea el ViewModel, carga los detalles y configura la UI con Compose.
     *
     * @param savedInstanceState Bundle con el estado guardado previo, si existe
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Obtiene el ID de la bicicleta pasado por el Intent o asigna "Unknown"
        bikeId = intent.getStringExtra("bikeId") ?: "Unknown"

        // Inicializar dependencias para el repositorio
        val context = applicationContext
        val bikeDatasource = AppDatabase.getDatabase(context).bikeDataSource()
        val bikeApiDataSource = BikeRemoteDataSource.getInstance()
        val bikeLocalDataSource = BikeLocalDataSource.getInstance(context)

        // Crear el repositorio con las fuentes de datos y contexto
        val repository = BikeRepository(
            bikeDatasource = bikeDatasource,
            remoteDataSource  = bikeApiDataSource,
            localDataSource  = bikeLocalDataSource,
            context = context,
        )

        // Crear el ViewModel usando ViewModelProvider y la factory personalizada
        viewModel = ViewModelProvider(
            this,
            DetailBikeViewModelFactory(repository)
        )[DetailBikeViewModel::class.java]

        // Solicitar al ViewModel cargar los detalles de la bicicleta indicada
        viewModel.loadBikeDetails(bikeId)

        // Configurar la UI usando Jetpack Compose
        setContent {
            PatinflyTheme {
                Column {
                    // Barra superior con botón para volver a la pantalla anterior
                    TopAppBar(navigateBack = { finish() })

                    // Obtenemos el estado de la UI desde el ViewModel de forma reactiva
                    val uiState = viewModel.uiState.collectAsState()

                    // Renderizar diferentes composables según el estado actual
                    when (val state = uiState.value) {
                        is DetailBikeUiState.Loading -> {
                        }
                        is DetailBikeUiState.Success -> {
                            DetailBikeScreen(
                                bike = state.bike,
                                viewModel = viewModel
                            )
                        }
                        is DetailBikeUiState.Error -> {
                            // Mostrar mensaje de error en logs si la carga falla
                            Log.e("DetailBikeActivity", "Error: ${state.message}")
                        }
                    }
                }
            }
        }
    }
}
