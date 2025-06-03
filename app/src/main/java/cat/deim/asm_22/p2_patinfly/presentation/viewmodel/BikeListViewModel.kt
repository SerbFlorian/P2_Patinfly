package cat.deim.asm_22.p2_patinfly.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import cat.deim.asm_22.p2_patinfly.domain.models.ServerStatus
import cat.deim.asm_22.p2_patinfly.domain.repository.IBikeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar la lógica de negocio para la lista de bicicletas.
 * Se encarga de cargar los datos desde el repositorio y exponerlos mediante StateFlows.
 *
 * @property repository Repositorio para obtener los datos de bicicletas.
 * @property context Contexto de la aplicación para operaciones relacionadas con el sistema.
 */
@SuppressLint("StaticFieldLeak")
class BikeListViewModel(
    private val repository: IBikeRepository,
    private val context: Context
) : ViewModel() {

    /**
     * Estado interno mutable que mantiene la lista actual de bicicletas.
     */
    private val _bikes = MutableStateFlow<List<Bike>>(emptyList())

    /**
     * Estado inmutable expuesto públicamente con la lista de bicicletas.
     */
    val bikes: StateFlow<List<Bike>> = _bikes.asStateFlow()

    /**
     * Estado interno mutable para el estado del servidor (puede ser null si no hay información).
     */
    private val _serverStatus = MutableStateFlow<ServerStatus?>(null)

    /**
     * Estado interno mutable para indicar si se está realizando una carga de datos.
     */
    private val _isLoading = MutableStateFlow(false)

    init {
        // Carga inicial de datos al crear el ViewModel
        loadData()
    }

    /**
     * Función privada para cargar datos desde el repositorio y obtener el estado del servidor.
     * Ejecuta la carga en un coroutine en el scope del ViewModel.
     *
     * - Actualiza el estado de carga (_isLoading).
     * - Observa el estado del servidor a través de LiveData.
     * - Carga la lista de bicicletas desde el repositorio.
     * - Captura y registra cualquier excepción que ocurra durante la carga.
     */
    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener estado del servidor usando LiveData (observado permanentemente)
                val liveData = ServerStatus.execute(context)
                liveData.observeForever { result ->
                    result.onSuccess { status ->
                        _serverStatus.value = status
                    }.onFailure { e ->
                        Log.e("BikeListVM", "Error getting server status", e)
                    }
                }

                // Obtener todas las bicicletas disponibles del repositorio
                _bikes.value = repository.getAll().toList()

            } catch (e: Exception) {
                Log.e("BikeListVM", "Error loading data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
