package cat.deim.asm_22.p2_patinfly.presentation.detailBike

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de manejar la lógica y el estado UI
 * para la pantalla de detalle de una bicicleta.
 *
 * @property bikeRepository Repositorio para obtener y actualizar datos de bicicletas.
 */
class DetailBikeViewModel(
    private val bikeRepository: BikeRepository
) : ViewModel() {

    /**
     * Estado interno mutable que representa el estado actual de la UI.
     */
    private val _uiState = MutableStateFlow<DetailBikeUiState>(DetailBikeUiState.Loading)

    /**
     * Estado de solo lectura expuesto para la UI.
     */
    val uiState: StateFlow<DetailBikeUiState> = _uiState.asStateFlow()

    /**
     * Carga los detalles de una bicicleta dado su UUID.
     * Actualiza el estado UI con el resultado de la carga.
     *
     * @param bikeUUID Identificador único de la bicicleta a cargar.
     */
    fun loadBikeDetails(bikeUUID: String) {
        viewModelScope.launch {
            try {
                val bike = bikeRepository.getBike(bikeUUID)
                if (bike != null) {
                    _uiState.value = DetailBikeUiState.Success(bike)
                } else {
                    _uiState.value = DetailBikeUiState.Error("Bike not found")
                }
            } catch (e: Exception) {
                _uiState.value = DetailBikeUiState.Error("Error loading bike details: ${e.message}")
            }
        }
    }

    /**
     * Cambia el estado de reserva de la bicicleta (activo/inactivo).
     * Actualiza el estado UI con la nueva información.
     *
     * @param bike Bicicleta cuyo estado de reserva será alternado.
     */
    fun toggleReservation(bike: Bike) {
        viewModelScope.launch {
            try {
                val newStatus = !bike.isActive
                bikeRepository.updateBikeStatus(bike.uuid, newStatus)
                // Actualiza el estado UI con la nueva bicicleta
                _uiState.value = DetailBikeUiState.Success(bike.copy(isActive = newStatus))
            } catch (e: Exception) {
                _uiState.value = DetailBikeUiState.Error("Error updating bike status: ${e.message}")
            }
        }
    }
}

/**
 * Representa los posibles estados UI para la pantalla de detalle de bicicleta.
 */
sealed class DetailBikeUiState {

    /**
     * Estado cuando la información se está cargando.
     */
    data object Loading : DetailBikeUiState()

    /**
     * Estado cuando la información se cargó correctamente.
     *
     * @property bike Objeto Bike con los datos cargados.
     */
    data class Success(val bike: Bike) : DetailBikeUiState()

    /**
     * Estado cuando ocurre un error al cargar o actualizar datos.
     *
     * @property message Mensaje de error descriptivo.
     */
    data class Error(val message: String) : DetailBikeUiState()
}
