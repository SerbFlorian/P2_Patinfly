package cat.deim.asm_22.p2_patinfly.data.datasource.remote

import cat.deim.asm_22.p2_patinfly.data.datasource.remote.model.BikeApiModel
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.model.LoginResponse
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.status.ServerStatusApiModel
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.model.UserApiModel
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.model.VehiclesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interfaz que define los endpoints para la comunicación con la API remota de Patinfly.
 *
 * Utiliza Retrofit para la definición de las llamadas HTTP.
 */
interface APIService {

    /**
     * Realiza la petición para autenticar un usuario.
     *
     * @param email Correo electrónico del usuario, enviado en el header "Email".
     * @param password Contraseña del usuario, enviada en el header "Password".
     * @param origin Origen de la petición, enviado en el header "Origin".
     * @return Una [Response] que envuelve un objeto [LoginResponse] con los datos del login.
     */
    @POST("api/login")
    suspend fun loginUser(
        @Header("Email") email: String,
        @Header("Password") password: String,
        @Header("Origin") origin: String
    ): Response<LoginResponse>

    /**
     * Obtiene la información del usuario autenticado.
     *
     * @param token Token de autorización en formato Bearer enviado en el header "Authorization".
     * @return Un objeto [UserApiModel] con los datos del usuario.
     */
    @GET("api/user")
    suspend fun getUser(@Header("Authorization") token: String): UserApiModel

    /**
     * Obtiene la lista de bicicletas asociadas al usuario.
     *
     * @param token Token de autorización en formato Bearer enviado en el header "Authorization".
     * @return Una [Response] que envuelve un objeto [VehiclesResponse] con la lista de bicicletas.
     */
    @GET("api/vehicle")
    suspend fun getBikes(@Header("Authorization") token: String): Response<VehiclesResponse>

    /**
     * Obtiene los detalles de una bicicleta específica por su ID.
     *
     * @param token Token de autorización en formato Bearer enviado en el header "Authorization".
     * @param id Identificador único de la bicicleta.
     * @return Una [Response] que envuelve un objeto [BikeApiModel] con los datos de la bicicleta.
     */
    @GET("api/vehicle/{id}")
    suspend fun getBikeById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<BikeApiModel>

    /**
     * Obtiene el estado actual del servidor.
     *
     * @return Un objeto [ServerStatusApiModel] con la información de estado del servidor.
     */
    @GET("api/status")
    suspend fun getServerStatus(): ServerStatusApiModel

    /**
     * Obtiene la lista de todos los usuarios.
     *
     * @return Una lista de objetos [UserApiModel] con los datos de todos los usuarios.
     */
    @GET("api/user")
    suspend fun getAllUsers(): List<UserApiModel>
}
