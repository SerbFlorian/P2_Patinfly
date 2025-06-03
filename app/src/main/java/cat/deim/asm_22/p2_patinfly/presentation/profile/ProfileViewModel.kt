package cat.deim.asm_22.p2_patinfly.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.deim.asm_22.p2_patinfly.data.datasource.database.UserDatasource
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.BikeDTO
import cat.deim.asm_22.p2_patinfly.data.datasource.local.model.UserModel
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de manejar la lógica de la pantalla de perfil de usuario.
 * Gestiona la carga de datos del usuario, las bicicletas activas y permite
 * actualizar el estado de las bicicletas y modificar el historial de alquileres.
 *
 * @property userDataSource Fuente de datos para obtener la información del usuario.
 * @property bikeRepository Repositorio para manejar las operaciones relacionadas con las bicicletas.
 */
class ProfileViewModel(
    private val userDataSource: UserDatasource,
    private val bikeRepository: BikeRepository
) : ViewModel() {

    /**
     * Estado interno mutable que representa la UI del perfil.
     */
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)

    /**
     * Estado público e inmutable para que la UI observe los cambios.
     */
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /**
     * Inicializa la carga del perfil del usuario al crear la instancia del ViewModel.
     */
    init {
        loadUserProfile()
    }

    /**
     * Carga la información del usuario desde la base de datos y las bicicletas activas.
     * Actualiza el estado de la UI según el resultado.
     *
     * Si el usuario no se encuentra, se emite un estado de error.
     * En caso de excepciones, se captura y se actualiza el estado de error con el mensaje.
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val userDTO = userDataSource.getUser()
                if (userDTO != null) {
                    val activeBikes = bikeRepository.getActiveBikes()
                    val userDomain = userDTO.toDomain()
                    val userModel = UserModel.fromDomain(userDomain, activeBikes)
                    _uiState.value = ProfileUiState.Success(userModel)
                } else {
                    _uiState.value = ProfileUiState.Error("User not found")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error loading user: ${e.message}")
            }
        }
    }

    /**
     * Actualiza el estado activo/inactivo de una bicicleta en el repositorio.
     *
     * @param bike La bicicleta cuyo estado se quiere actualizar.
     *
     * En caso de error, actualiza el estado de UI a un estado de error con el mensaje.
     */
    fun updateBikeStatus(bike: BikeDTO) {
        viewModelScope.launch {
            try {
                val newStatus = bike.isActive
                bikeRepository.updateBikeStatus(bike.uuid, newStatus)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error updating bike status: ${e.message}")
            }
        }
    }

    /**
     * Elimina una bicicleta del historial de alquileres del usuario en el estado actual de la UI.
     *
     * @param bikeId El UUID de la bicicleta que se quiere eliminar del historial.
     *
     * Esta operación solo afecta al estado UI local, no modifica la base de datos.
     */
    fun removeBikeFromHistory(bikeId: String) {
        _uiState.update { currentState ->
            when (currentState) {
                is ProfileUiState.Success -> {
                    val updatedUser = currentState.user?.copy(
                        rentalHistory = currentState.user.rentalHistory.filter { it.uuid != bikeId }
                    )
                    currentState.copy(user = updatedUser)
                }
                else -> currentState
            }
        }
    }
}

/**
 * Estados posibles de la UI del perfil de usuario.
 */
sealed class ProfileUiState {

    /** Estado que indica que la información se está cargando. */
    data object Loading : ProfileUiState()

    /**
     * Estado que indica que la carga fue exitosa.
     *
     * @property user Modelo con la información del usuario, puede ser nulo.
     */
    data class Success(val user: UserModel?) : ProfileUiState()

    /**
     * Estado que indica que ocurrió un error durante la carga o actualización.
     *
     * @property message Mensaje descriptivo del error.
     */
    data class Error(val message: String) : ProfileUiState()
}
