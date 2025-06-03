package cat.deim.asm_22.p2_patinfly.presentation.screen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import cat.deim.asm_22.p2_patinfly.presentation.ui.theme.PatinflyTheme
import cat.deim.asm_22.p2_patinfly.presentation.components.TopAppBar
import cat.deim.asm_22.p2_patinfly.presentation.viewmodel.BikeListViewModel
import cat.deim.asm_22.p2_patinfly.presentation.viewmodel.BikeListViewModelFactory
import kotlinx.coroutines.launch

/**
 * Actividad que muestra la lista de bicicletas disponibles en la aplicación.
 *
 * Se encarga de configurar la UI con Jetpack Compose, manejar la obtención del ViewModel,
 * filtrar las bicicletas según categorías recibidas por el Intent, y mostrar la lista resultante.
 */
class BikeListActivity : ComponentActivity() {

    /**
     * Metodo llamado al crear la actividad.
     * Configura el contenido composable de la pantalla, obtiene el ViewModel y
     * carga la lista de bicicletas filtradas según categorías o tipo recibidos.
     *
     * @param savedInstanceState Estado previo de la actividad, si lo hay.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Aplicar tema personalizado
            PatinflyTheme {
                val context = LocalContext.current
                // Crear instancia del repositorio para acceder a datos de bicicletas
                val repository = BikeRepository.create(context)

                // Crear ViewModel usando una factory para pasar dependencias
                val viewModel: BikeListViewModel = viewModel(
                    factory = BikeListViewModelFactory(
                        repository = repository,
                        context = context
                    )
                )

                // Estado observable de la lista completa de bicicletas
                val bikes by viewModel.bikes.collectAsState()

                // CoroutineScope para lanzar tareas asíncronas dentro del composable
                val coroutineScope = rememberCoroutineScope()

                // Estado local para la lista filtrada de bicicletas, inicialmente vacía
                var filteredBikes by remember { mutableStateOf<List<Bike>>(emptyList()) }

                // Obtener las categorías o categoría única enviadas por el Intent
                val categories = intent.getStringArrayListExtra("categories")
                val category = intent.getStringExtra("category")

                Log.d("BikeListActivity", "Categories recibidas: $categories, category: $category")

                // Lanzar efecto cuando cambian las categorías recibidas para cargar bicicletas filtradas
                LaunchedEffect(categories, category) {
                    coroutineScope.launch {
                        filteredBikes = when {
                            // Si se reciben varias categorías, obtener bicicletas de todas ellas y aplanar la lista
                            categories != null -> {
                                categories.flatMap { cat ->
                                    repository.getBikesByCategory(cat).onEach { bike ->
                                        Log.d("FILTER", "Bike ${bike.name} type: ${bike.bikeTypeName}")
                                    }
                                }
                            }
                            // Si se recibe una categoría única diferente de "all", obtener bicicletas de esa categoría
                            category != null && category != "all" -> {
                                repository.getBikesByCategory(category)
                            }
                            // Si no se recibe categoría, mostrar todas las bicicletas disponibles
                            else -> {
                                bikes
                            }
                        }
                        Log.d("BikeListActivity", "Filtered bikes loaded: ${filteredBikes.size}")
                    }
                }

                // Estructura visual: columna con barra superior y lista de bicicletas filtradas
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(navigateBack = { finish() }) // Botón para volver atrás
                    BikeListScreen(bikes = filteredBikes) // Composable que muestra la lista de bicicletas
                }
            }
        }
    }
}
