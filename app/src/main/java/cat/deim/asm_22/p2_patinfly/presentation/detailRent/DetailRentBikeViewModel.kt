package cat.deim.asm_22.p2_patinfly.presentation.detailRent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la lógica de la pantalla de detalle de alquiler de bicicleta.
 *
 * @property bikeRepository Repositorio para obtener y actualizar datos de bicicletas.
 */
class DetailRentBikeViewModel(
    private val bikeRepository: BikeRepository
) : ViewModel() {

    /**
     * Estado interno mutable que representa el estado actual de la UI.
     */
    private val _uiState = MutableStateFlow<DetailRentBikeUiState>(DetailRentBikeUiState.Loading)

    /**
     * Estado público e inmutable que expone el estado actual de la UI.
     */
    val uiState: StateFlow<DetailRentBikeUiState> = _uiState.asStateFlow()

    /**
     * Carga los detalles de una bicicleta específica a partir de su UUID.
     *
     * @param bikeUUID El UUID de la bicicleta a cargar.
     */
    fun loadBikeDetails(bikeUUID: String) {
        viewModelScope.launch {
            val bike = bikeRepository.getBike(bikeUUID)
            if (bike != null) {
                // Actualiza el estado UI con la bicicleta cargada correctamente.
                _uiState.value = DetailRentBikeUiState.Success(bike)
            } else {
                // Actualiza el estado UI indicando que no se encontró la bicicleta.
                _uiState.value = DetailRentBikeUiState.Error("Bike not found")
            }
        }
    }

    /**
     * Actualiza el estado de alquiler de la bicicleta, alternando entre alquilada y no alquilada.
     *
     * @param bike La bicicleta cuyo estado de alquiler se desea actualizar.
     */
    fun updateBikeRentStatus(bike: Bike) {
        viewModelScope.launch {
            try {
                val newStatus = !bike.isRented
                bikeRepository.updateBikeRentStatus(bike.uuid, newStatus)
                // Actualiza el estado UI reflejando el nuevo estado de alquiler.
                _uiState.value = DetailRentBikeUiState.Success(bike.copy(isRented = newStatus))
            } catch (e: Exception) {
                // Maneja cualquier error durante la actualización y notifica a la UI.
                _uiState.value =
                    DetailRentBikeUiState.Error("Error updating bike status: ${e.message}")
            }
        }
    }

    /**
     * Carga la primera bicicleta activa disponible.
     */
    fun loadFirstActiveBike() {
        viewModelScope.launch {
            _uiState.value = DetailRentBikeUiState.Loading
            val activeBike = bikeRepository.getFirstActiveBike()
            // Actualiza el estado UI con la bicicleta activa encontrada o null si no hay ninguna.
            _uiState.value = DetailRentBikeUiState.Success(activeBike)
        }
    }
}

/**
 * Clase sellada que representa los posibles estados de la UI para la pantalla de detalle de bicicleta.
 */
sealed class DetailRentBikeUiState {
    /**
     * Estado que indica que los datos se están cargando.
     */
    data object Loading : DetailRentBikeUiState()

    /**
     * Estado que indica que los datos se han cargado correctamente.
     *
     * @property bike La bicicleta cargada o null si no hay ninguna.
     */
    data class Success(val bike: Bike?) : DetailRentBikeUiState()

    /**
     * Estado que indica que ha ocurrido un error.
     *
     * @property message Mensaje de error descriptivo.
     */
    data class Error(val message: String) : DetailRentBikeUiState()
}
