package cat.deim.asm_22.p2_patinfly.domain.models

import java.util.UUID

/**
 * Representa un usuario del sistema con información de autenticación y detalles personales.
 *
 * @property uuid Identificador único universal (UUID) del usuario.
 * @property name Nombre completo del usuario.
 * @property email Correo electrónico del usuario.
 * @property hashedPassword Contraseña del usuario almacenada en forma hasheada para seguridad.
 * @property creationDate Fecha en la que se creó la cuenta del usuario.
 * @property lastConnection Fecha y hora de la última conexión del usuario.
 * @property deviceId Identificador del dispositivo utilizado por el usuario.
 * @property accessToken Token de acceso para autenticación temporal.
 * @property refreshToken Token utilizado para renovar el token de acceso.
 * @property expirationToken Fecha y hora en que expira el token de acceso.
 * @property expiresRefresh Fecha y hora en que expira el token de renovación (refresh token).
 * @property group Grupo o rol al que pertenece el usuario (por ejemplo, admin, usuario estándar).
 */
data class User(
    val uuid: UUID,
    val name: String,
    val email: String,
    val hashedPassword: String,
    val creationDate: String,
    val lastConnection: String,
    val deviceId: String,
    val accessToken: String,
    val refreshToken: String,
    val expirationToken: String,
    val expiresRefresh: String,
    val group: String
)
