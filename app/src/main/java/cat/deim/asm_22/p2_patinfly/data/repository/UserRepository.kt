package cat.deim.asm_22.p2_patinfly.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import cat.deim.asm_22.p2_patinfly.data.datasource.database.AppDatabase
import cat.deim.asm_22.p2_patinfly.data.datasource.database.UserDatasource
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.UserDTO
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.network.RetrofitClient
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.network.RetrofitClient.apiService
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.model.UserApiModel
import cat.deim.asm_22.p2_patinfly.domain.models.User
import cat.deim.asm_22.p2_patinfly.domain.repository.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import retrofit2.HttpException
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Implementación concreta de IUserRepository que maneja las operaciones de usuario,
 * combinando fuentes de datos locales (Room Database) y remotas (API REST).
 *
 * @property userDatasource Fuente de datos local para operaciones con la base de datos
 * @property context Contexto de la aplicación para acceso a recursos y servicios del sistema
 */
class UserRepository(
    private val userDatasource: UserDatasource,
    private val context: Context
) : IUserRepository {

    companion object {
        // Nombre del archivo de preferencias compartidas
        private const val PREFS_NAME = "PatinflyPrefs"
        // Clave para almacenar el token de autenticación en SharedPreferences
        private const val KEY_AUTH_TOKEN = ""
        // Token de autenticación en memoria
        var authToken: String? = null

        /**
         * Crea una instancia de UserRepository inicializando las dependencias necesarias.
         *
         * @param context Contexto de la aplicación
         * @return Instancia de IUserRepository configurada
         */
        fun create(context: Context): IUserRepository {
            // Inicialización de la base de datos local
            val dataSource = AppDatabase.getDatabase(context).userDataSource()
            // Configuración de SharedPreferences para persistencia del token
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            // Recuperar token almacenado (si existe)
            authToken = prefs.getString(KEY_AUTH_TOKEN, null)
            // Inicializar Retrofit con el proveedor de token
            RetrofitClient.initialize { authToken }
            Log.d("UserRepository", "Token restaurado desde prefs: ${authToken?.take(10)}...")
            return UserRepository(dataSource, context)
        }
    }

    // Lazy initialization de SharedPreferences
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Establece el token de autenticación y lo persiste en SharedPreferences.
     *
     * @param token Token de autenticación a almacenar
     */
    private fun setAuthToken(token: String?) {
        authToken = token
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
        Log.d("UserRepository", "Auth token guardado: ${token?.take(10)}...")
    }

    /**
     * Obtiene el token de autenticación actual.
     *
     * @return Token de autenticación o null si no existe
     */
    fun getAuthToken(): String? {
        Log.d("UserRepository", "Token recuperado: ${authToken?.take(10)}...")
        return authToken
    }

    /**
     * Guarda un usuario en la base de datos local.
     *
     * @param user Usuario a guardar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    override suspend fun setUser(user: User): Boolean {
        return try {
            userDatasource.save(UserDTO.fromDomain(user))
            Log.d("UserRepository", "Usuario ${user.email} guardado localmente: ${userDatasource.getByEmail(user.email) != null}")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error guardando usuario localmente: ${e.message}", e)
            false
        }
    }

    /**
     * Obtiene el usuario actual desde la base de datos local.
     *
     * @return Usuario encontrado o null si no existe
     */
    override suspend fun getUser(): User? {
        val user = userDatasource.getUser()?.toDomain()
        if (user != null) {
            Log.d("UserRepository", "Usuario recuperado de la base de datos local: ${user.email}")
        } else {
            Log.d("UserRepository", "No se encontró ningún usuario en la base de datos local.")
        }
        return user
    }

    /**
     * Obtiene un usuario por su email, primero buscando localmente y luego en la API si es necesario.
     *
     * @param email Email del usuario a buscar
     * @return Usuario encontrado o null si no existe
     * @throws Exception Si ocurre un error al acceder a la API
     */
    override suspend fun getUser(email: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Intentar obtener usuario localmente primero
                val localUser = userDatasource.getByEmail(email)?.toDomain()
                if (localUser != null) {
                    Log.d("UserRepository", "Usuario encontrado en local: ${localUser.email}")
                    return@withContext localUser
                }

                // 2. Si no está localmente y hay conexión a internet, buscar en API
                if (isNetworkAvailable() && !getAuthToken().isNullOrEmpty()) {
                    Log.d("UserRepository", "No hay usuario local. Intentando obtener desde API.")
                    val token = getAuthToken() ?: return@withContext null
                    Log.d("UserRepository", "Llamando a getUser con token: ${token.take(10)}...")

                    val userApiModel = try {
                        apiService.getUser("Bearer $token")
                    } catch (e: HttpException) {
                        Log.e("UserRepository", "Error HTTP al obtener usuario: Código=${e.code()}, Mensaje=${e.message()}", e)
                        throw Exception("Error HTTP al obtener usuario: ${e.code()} - ${e.message()}")
                    } catch (e: Exception) {
                        Log.e("UserRepository", "Error al obtener usuario de la API: ${e.message}", e)
                        throw Exception("Error al obtener usuario de la API: ${e.message}")
                    }

                    Log.d("UserRepository", "Datos de usuario obtenidos de API: $userApiModel")
                    if (!userApiModel.email.isNullOrEmpty()) {
                        val userFromApi = userApiModel.toDomain()
                        userDatasource.save(UserDTO.fromDomain(userFromApi))
                        Log.d("UserRepository", "Usuario ${userFromApi.email} obtenido y guardado de la API.")
                        return@withContext userFromApi
                    } else {
                        Log.e("UserRepository", "API retornó datos vacíos o inválidos: $userApiModel")
                        throw Exception("API retornó datos vacíos o inválidos para el usuario.")
                    }
                } else {
                    Log.d("UserRepository", "No hay red o token para obtener el usuario.")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Error obteniendo usuario: ${e.message}", e)
                throw e
            }
        }
    }

    /**
     * Actualiza un usuario en la base de datos local.
     *
     * @param user Usuario con los datos actualizados
     * @return Usuario actualizado
     */
    override suspend fun updateUser(user: User): User {
        setUser(user)
        Log.d("UserRepository", "Usuario ${user.email} actualizado.")
        return user
    }

    /**
     * Verifica si hay conexión de red disponible.
     *
     * @return true si hay conexión disponible, false en caso contrario
     */
    @SuppressLint("ServiceCast")
    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }

    /**
     * Elimina el usuario actual de la base de datos local.
     *
     * @return Usuario eliminado o null si no había usuario
     */
    override suspend fun deleteUser(): User? {
        val user = getUser()
        user?.let {
            userDatasource.delete(UserDTO.fromDomain(it))
            Log.d("UserRepository", "Usuario ${it.email} eliminado localmente.")
        } ?: Log.d("UserRepository", "No hay usuario para eliminar.")
        return user
    }

    /**
     * Obtiene todos los usuarios, primero de la base de datos local y sincroniza con la API si es necesario.
     *
     * @return Lista de usuarios
     */
    override suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Obtener usuarios locales primero
                val localUsers = userDatasource.getAll().map { it.toDomain() }
                if (localUsers.isNotEmpty()) {
                    Log.d("UserRepository", "Usuarios encontrados localmente: ${localUsers.size}")
                    return@withContext localUsers
                }

                // 2. Si no hay locales y hay conexión, sincronizar con API
                if (isNetworkAvailable() && !getAuthToken().isNullOrEmpty()) {
                    Log.d("UserRepository", "No hay usuarios locales, sincronizando desde API.")
                    val success = syncAllUsersFromApi()
                    if (success) {
                        val updatedLocalUsers = userDatasource.getAll().map { it.toDomain() }
                        Log.d("UserRepository", "Usuarios sincronizados: ${updatedLocalUsers.size}")
                        return@withContext updatedLocalUsers
                    }
                }
                Log.d("UserRepository", "No se pudieron obtener usuarios localmente ni de la red.")
                return@withContext emptyList()
            } catch (e: Exception) {
                Log.e("UserRepository", "Error al obtener todos los usuarios: ${e.message}", e)
                return@withContext emptyList()
            }
        }
    }

    /**
     * Realiza el login del usuario contra la API y guarda los datos localmente.
     *
     * @param email Email del usuario
     * @param password Contraseña sin encriptar
     * @param origin Origen de la petición (ej. "app")
     * @return Usuario autenticado
     * @throws Exception Si el login falla
     */
    suspend fun loginUser(email: String, password: String, origin: String): User {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("UserRepository", "Intentando login para email: $email")
                val response = apiService.loginUser(email, password, origin)
                Log.d("UserRepository", "Respuesta API login - Código: ${response.code()}, Exitosa: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.d("UserRepository", "Cuerpo de la respuesta: $loginResponse")
                    val token = loginResponse?.token?.access

                    if (token != null) {
                        setAuthToken(token)
                        Log.d("UserRepository", "Login exitoso. Token recibido: ${token.take(10)}...")

                        // Crear objeto de usuario con datos del login
                        val currentTime = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                        val userFromLogin = UserApiModel(
                            id = loginResponse.token.id,
                            email = email,
                            accessToken = token,
                            expirationToken = loginResponse.token.expires,
                            refreshToken = loginResponse.token.refresh,
                            expiresRefresh = loginResponse.token.expiresRefresh
                        )

                        Log.d("UserRepository", "Usuario creado desde login: $userFromLogin")

                        // Intentar obtener datos adicionales del usuario desde la API
                        val userFromApi = try {
                            Log.d("UserRepository", "Llamando a getUser con token: ${token.take(10)}...")
                            val apiResponse = apiService.getUser("Bearer $token")
                            Log.d("UserRepository", "Datos de usuario obtenidos de API: $apiResponse")
                            if (!apiResponse.email.isNullOrEmpty()) {
                                userFromLogin.mergeWith(apiResponse)
                            } else {
                                userFromLogin
                            }
                        } catch (e: Exception) {
                            Log.e("UserRepository", "Error al obtener usuario de la API: ${e.message}", e)
                            userFromLogin
                        }

                        // Hashear contraseña antes de guardar
                        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
                        val userDomain = userFromApi.toDomain(hashedPassword).copy(
                            creationDate = currentTime,
                            lastConnection = currentTime,
                            group = "ASM22" // Grupo fijo para este proyecto
                        )

                        userDatasource.save(UserDTO.fromDomain(userDomain))
                        Log.d("UserRepository", "Usuario ${userDomain.email} obtenido y guardado con contraseña hasheada.")
                        return@withContext userDomain
                    } else {
                        Log.e("UserRepository", "No se encontró token en la respuesta del login.")
                        throw Exception("No se encontró token en la respuesta del login.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UserRepository", "Error en login - Código: ${response.code()}, Error: $errorBody")
                    throw Exception("Error en login - Código: ${response.code()}, Error: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Excepción durante login: ${e.message}", e)
                throw e
            }
        }
    }

    /**
     * Sincroniza todos los usuarios desde la API y los guarda localmente.
     *
     * @return true si la sincronización fue exitosa, false en caso contrario
     */
    private suspend fun syncAllUsersFromApi(): Boolean {
        return withContext(Dispatchers.IO) {
            // Verificar condiciones para sincronización
            if (!isNetworkAvailable()) {
                Log.d("UserRepository", "No hay red para sincronizar usuarios.")
                return@withContext false
            }

            if (getAuthToken().isNullOrEmpty()) {
                Log.d("UserRepository", "No hay token, se requiere login.")
                return@withContext false
            }

            try {
                Log.d("UserRepository", "Iniciando sincronización usuarios desde API.")
                val usersApiModelList = apiService.getAllUsers()

                if (usersApiModelList.isNotEmpty()) {
                    // Guardar cada usuario en la base de datos local
                    usersApiModelList.forEach { userApiModel ->
                        val userFromApi = userApiModel.toDomain()
                        userDatasource.save(UserDTO.fromDomain(userFromApi))
                        Log.d("UserRepository", "Sincronizado usuario: ${userFromApi.email}")
                    }
                    Log.d("UserRepository", "Sincronización completa. ${usersApiModelList.size} usuarios guardados.")
                    return@withContext true
                } else {
                    Log.d("UserRepository", "API devolvió lista vacía de usuarios.")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Error sincronizando usuarios: ${e.message}", e)
                return@withContext false
            }
        }
    }
}