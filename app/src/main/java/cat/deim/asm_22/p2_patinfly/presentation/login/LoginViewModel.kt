package cat.deim.asm_22.p2_patinfly.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.deim.asm_22.p2_patinfly.data.repository.UserRepository
import cat.deim.asm_22.p2_patinfly.domain.models.Credentials
import cat.deim.asm_22.p2_patinfly.domain.repository.IUserRepository
import cat.deim.asm_22.p2_patinfly.domain.usecase.LoginUsecase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar la lógica de negocio de la pantalla de login.
 * Mantiene el estado UI y maneja las llamadas al caso de uso de login.
 *
 * @property loginUseCase Caso de uso que maneja la lógica de login.
 * @property userRepository Repositorio para obtener información del usuario y token.
 */
class LoginViewModel(
    private val loginUseCase: LoginUsecase,
    private val userRepository: IUserRepository
) : ViewModel() {

    // Estado UI observable que mantiene email, password y validaciones
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Estado observable que mantiene el resultado actual del login (idle, loading, success, error)
    val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult.asStateFlow()

    /**
     * Actualiza el email en el estado y verifica si el usuario existe localmente.
     *
     * @param email Email ingresado por el usuario.
     */
    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
        if (email.isNotEmpty()) {
            viewModelScope.launch {
                // Comprueba si el usuario existe localmente para validar el email
                val userExists = loginUseCase.checkLocalUserExists(email)
                _uiState.value = _uiState.value.copy(isUserValid = userExists)
            }
        }
    }

    /**
     * Actualiza la contraseña en el estado y verifica si es válida localmente.
     *
     * @param password Contraseña ingresada por el usuario.
     */
    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
        if (password.isNotEmpty()) {
            viewModelScope.launch {
                val credentials = _uiState.value.run { Credentials(email, password) }
                // Valida localmente si la contraseña coincide con el usuario
                val isValid = loginUseCase.checkLocalPassword(credentials)
                _uiState.value = _uiState.value.copy(isPasswordValid = isValid)
            }
        }
    }

    /**
     * Realiza el proceso de login llamando al caso de uso correspondiente.
     * Actualiza el estado de loginResult según el resultado.
     *
     * @param email Email para login.
     * @param password Contraseña para login.
     */
    fun login(email: String, password: String) {
        _loginResult.value = LoginResult.Loading
        viewModelScope.launch {
            val result = loginUseCase.executeLogin(email, password, "android")
            result.onSuccess { message ->
                // Logueo exitoso, se obtiene token para debug
                Log.d("LoginViewModel", "Login exitoso, token: ${(userRepository as UserRepository).getAuthToken()?.take(10)}...")
                _loginResult.value = LoginResult.Success(message)
            }.onFailure { error ->
                Log.e("LoginViewModel", "Login fallido: ${error.message}")
                _loginResult.value = LoginResult.Error(
                    error.message ?: "Error de autenticación. Verifica tus credenciales."
                )
            }
        }
    }

    /**
     * Clase sellada que representa los posibles estados del resultado de login.
     */
    sealed class LoginResult {
        /** Estado inicial o sin acción */
        data object Idle : LoginResult()

        /** Estado cuando se está realizando el login */
        data object Loading : LoginResult()

        /**
         * Estado de éxito en login.
         * @param message Mensaje de éxito, usualmente token o confirmación.
         */
        data class Success(val message: String) : LoginResult()

        /**
         * Estado de error en login.
         * @param message Mensaje que describe el error ocurrido.
         */
        data class Error(val message: String) : LoginResult()
    }

    /**
     * Clase que representa el estado de la UI para la pantalla de login.
     *
     * @property email Email ingresado por el usuario.
     * @property password Contraseña ingresada por el usuario.
     * @property isUserValid Indica si el email existe y es válido.
     * @property isPasswordValid Indica si la contraseña es válida para el usuario.
     */
    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val isUserValid: Boolean = false,
        val isPasswordValid: Boolean = false
    )
}
