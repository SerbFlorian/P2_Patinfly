package cat.deim.asm_22.p2_patinfly.domain.usecase

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.mindrot.jbcrypt.BCrypt
import cat.deim.asm_22.p2_patinfly.domain.models.Credentials
import cat.deim.asm_22.p2_patinfly.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso para gestionar el proceso de login de usuarios.
 * Provee métodos para realizar login, verificar existencia local del usuario
 * y validar contraseñas almacenadas localmente.
 *
 * @property userRepository Repositorio para acceder a datos de usuario.
 * @property context Contexto de Android para acceso a SharedPreferences.
 */
class LoginUsecase(
    private val userRepository: UserRepository,
    private val context: Context
) {

    /**
     * SharedPreferences para almacenar datos locales de usuarios como contraseñas hasheadas.
     */
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("PatinflyPrefs", Context.MODE_PRIVATE)
    }

    /**
     * Ejecuta el proceso de login para un usuario dado un email y contraseña.
     *
     * - Intenta autenticar contra el repositorio.
     * - Si es exitoso, guarda la contraseña hasheada en SharedPreferences.
     * - Registra logs para seguimiento.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña en texto plano para autenticación.
     * @param origin Origen del login (p.ej., "manual", "auto").
     * @return [Result] que contiene mensaje de éxito o excepción en fallo.
     */
    suspend fun executeLogin(email: String, password: String, origin: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("LoginUseCase", "Intentando login para email: $email, origin: $origin")
            val user = userRepository.loginUser(email, password, origin)
            run {
                // Guardar la contraseña hasheada para futuros logins automáticos
                prefs.edit().putString("hashed_password_$email", user.hashedPassword).apply()
                Log.d("LoginUseCase", "Login exitoso para: ${user.email}. Perfil recuperado.")
                Log.d("LoginUseCase", "Token actual: ${userRepository.getAuthToken()?.take(10)}...")
                Result.success("Login exitoso para ${user.email}")
            }
        } catch (error: Exception) {
            Log.e("LoginUseCase", "Error durante el login: ${error.message}", error)
            Result.failure(Exception("Error durante el login: ${error.message}"))
        }
    }

    /**
     * Verifica si un usuario con el email dado existe localmente o puede autenticarse automáticamente.
     *
     * - Primero consulta localmente.
     * - Si no está y hay red disponible pero no hay token, intenta login automático usando
     *   contraseña almacenada o contraseña por defecto.
     *
     * @param email Correo electrónico del usuario a verificar.
     * @return `true` si el usuario existe o pudo autenticarse localmente, `false` si no.
     */
    suspend fun checkLocalUserExists(email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("LoginUseCase", "Verificando existencia local para: $email")
            val user = userRepository.getUser(email)
            if (user != null) {
                Log.d("LoginUseCase", "Usuario $email encontrado localmente")
                return@withContext true
            }
            // Verifica si hay conexión de red y no hay token para intentar login automático
            if (userRepository.isNetworkAvailable() && userRepository.getAuthToken().isNullOrEmpty()) {
                val storedHashedPassword = prefs.getString("hashed_password_$email", null)
                if (storedHashedPassword != null) {
                    Log.d("LoginUseCase", "Intentando login automático para: $email")
                    Log.d("LoginUseCase", "Login automático exitoso, usuario guardado localmente")
                    return@withContext true
                } else {
                    Log.d("LoginUseCase", "No hay contraseña almacenada para login automático")
                    val defaultPassword = ""
                    val loginResult = userRepository.loginUser(email, defaultPassword, "auto")
                    Log.d("LoginUseCase", "Login automático con contraseña por defecto exitoso")
                    prefs.edit().putString("hashed_password_$email", loginResult.hashedPassword).apply()
                    return@withContext true
                }
            }
            Log.d("LoginUseCase", "Usuario $email no encontrado localmente")
            return@withContext false
        } catch (error: Exception) {
            Log.e("LoginUseCase", "Error verificando existencia: ${error.message}", error)
            false
        }
    }

    /**
     * Verifica si la contraseña proporcionada en las credenciales coincide con la almacenada localmente.
     *
     * @param credentials Objeto [Credentials] con email y contraseña en texto plano.
     * @return `true` si la contraseña es correcta, `false` en caso contrario o error.
     */
    suspend fun checkLocalPassword(credentials: Credentials): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("LoginUseCase", "Verificando contraseña local para: ${credentials.email}")
            val user = userRepository.getUser(credentials.email)
            if (user == null) {
                Log.d("LoginUseCase", "Usuario no encontrado localmente para: ${credentials.email}")
                return@withContext false
            }
            // Verificación segura usando BCrypt
            verifyPassword(credentials.password, user.hashedPassword)
        } catch (error: Exception) {
            Log.e("LoginUseCase", "Error verificando contraseña: ${error.message}", error)
            false
        }
    }

    /**
     * Verifica si la contraseña de entrada coincide con la contraseña almacenada hasheada.
     *
     * @param inputPassword Contraseña en texto plano proporcionada por el usuario.
     * @param storedPassword Contraseña hasheada almacenada.
     * @return `true` si coinciden, `false` si no o en caso de error.
     */
    private fun verifyPassword(inputPassword: String, storedPassword: String): Boolean {
        try {
            Log.d("LoginUseCase", "Verificando contraseña")
            return BCrypt.checkpw(inputPassword, storedPassword)
        } catch (e: Exception) {
            Log.e("LoginUseCase", "Error verificando contraseña: ${e.message}", e)
            return false
        }
    }
}
