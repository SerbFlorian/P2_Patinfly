package cat.deim.asm_22.p2_patinfly.presentation.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cat.deim.asm_22.p2_patinfly.data.datasource.database.AppDatabase
import cat.deim.asm_22.p2_patinfly.data.repository.UserRepository
import cat.deim.asm_22.p2_patinfly.domain.usecase.LoginUsecase
import cat.deim.asm_22.p2_patinfly.presentation.ui.theme.PatinflyTheme
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.network.RetrofitClient

/**
 * Activity para manejar la pantalla de login.
 *
 * Se encarga de inicializar los componentes necesarios para el login,
 * incluyendo la configuración del repositorio, el caso de uso y la interfaz de usuario.
 */
class LoginActivity : ComponentActivity() {

    /**
     * Metodo llamado al crear la Activity.
     *
     * Inicializa la interfaz de usuario con Jetpack Compose,
     * habilita el modo Edge-to-Edge para la UI,
     * inicializa RetrofitClient con el token de autenticación,
     * crea el repositorio de usuario y el caso de uso de login,
     * y finalmente muestra la pantalla de login.
     *
     * @param savedInstanceState Estado previo guardado, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatinflyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val userDataSource = AppDatabase.getDatabase(context).userDataSource()
                    val userRepository = UserRepository(userDataSource, context)

                    // IMPORTANTE: Inicializa RetrofitClient proporcionando el proveedor del token
                    RetrofitClient.initialize { userRepository.getAuthToken() }

                    val loginUseCase = LoginUsecase(
                        userRepository,
                        context = context
                    )

                    LoginScreen(
                        viewModel = LoginViewModel(
                            loginUseCase,
                            userRepository = userRepository
                        )
                    )
                }
            }
        }
    }
}
