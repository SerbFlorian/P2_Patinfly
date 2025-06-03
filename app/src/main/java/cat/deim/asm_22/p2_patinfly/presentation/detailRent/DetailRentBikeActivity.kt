package cat.deim.asm_22.p2_patinfly.presentation.detailRent

import android.os.Bundle
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
 * Actividad principal para la pantalla de detalle y alquiler de bicicletas.
 *
 * Esta actividad inicializa las dependencias necesarias, configura el ViewModel,
 * y establece la interfaz de usuario usando Jetpack Compose.
 */
class DetailRentBikeActivity : ComponentActivity() {
    private lateinit var bikeId: String
    private lateinit var viewModel: DetailRentBikeViewModel

    /**
     * Metodo llamado cuando la actividad es creada.
     *
     * Configura el modo Edge-to-Edge para la interfaz, obtiene el ID de la bicicleta
     * desde el intent, inicializa el repositorio y el ViewModel, carga los detalles
     * de la bicicleta y finalmente establece el contenido UI con Compose.
     *
     * @param savedInstanceState Estado guardado de la actividad, si está disponible.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita el modo Edge-to-Edge para extender la UI hasta los bordes de la pantalla
        enableEdgeToEdge()

        // Obtén el ID de la bicicleta desde el intent
        bikeId = intent.getStringExtra("bikeId") ?: "Unknown"

        // Inicializar dependencias necesarias para el repositorio
        val context = applicationContext
        val bikeDatasource = AppDatabase.getDatabase(context).bikeDataSource()
        val bikeApiDataSource = BikeRemoteDataSource.getInstance()
        val bikeLocalDataSource = BikeLocalDataSource.getInstance(context)

        // Crear el repositorio con las fuentes de datos necesarias
        val repository = BikeRepository(
            bikeDatasource = bikeDatasource,
            remoteDataSource  = bikeApiDataSource,
            localDataSource  = bikeLocalDataSource,
            context = context,
        )

        // Usar ViewModelFactory para crear el ViewModel con las dependencias inyectadas
        viewModel = ViewModelProvider(
            this,
            DetailRentBikeViewModelFactory(repository)
        )[DetailRentBikeViewModel::class.java]

        // Cargar los detalles de la bicicleta utilizando el ViewModel
        viewModel.loadBikeDetails(bikeId)

        // Establecer el contenido UI usando Jetpack Compose
        setContent {
            PatinflyTheme {
                Column {
                    TopAppBar(navigateBack = { finish() })

                    val uiState = viewModel.uiState.collectAsState()

                    // Mostrar la pantalla de detalles del alquiler pasando el estado y ViewModel
                    DetailRentBikeScreen(uiState = uiState.value, bikeViewModel = viewModel)
                }
            }
        }
    }
}
