package cat.deim.asm_22.p2_patinfly.data.datasource.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Modelo que representa la respuesta de la API tras un intento de login.
 *
 * @property success Indica si el login fue exitoso.
 * @property token Objeto que contiene los datos del token de autenticación.
 * @property version Versión de la API o del sistema en la respuesta.
 */
@Serializable
data class LoginResponse(
    val success: Boolean,
    val token: TokenData,
    val version: String
)

/**
 * Modelo que representa los datos del token de autenticación recibido tras el login.
 *
 * @property id Identificador único del usuario.
 * @property email Email asociado al usuario autenticado.
 * @property access Token de acceso (access token) para autenticar peticiones.
 * @property expires Fecha y hora de expiración del token de acceso.
 * @property refresh Token para refrescar el token de acceso.
 * @property expiresRefresh Fecha y hora de expiración del token de refresco.
 */
@Serializable
data class TokenData(
    val id: Int,
    val email: String,
    val access: String,
    val expires: String,
    val refresh: String,
    @SerialName("expires_refresh")
    val expiresRefresh: String
)
