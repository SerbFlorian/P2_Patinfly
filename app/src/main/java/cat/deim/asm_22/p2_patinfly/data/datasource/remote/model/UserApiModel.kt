package cat.deim.asm_22.p2_patinfly.data.datasource.remote.model

import cat.deim.asm_22.p2_patinfly.domain.models.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.random.Random

/**
 * Modelo de datos que representa la información de un usuario recibida desde la API remota.
 *
 * @property id Identificador numérico del usuario (por defecto -1 si no existe).
 * @property name Nombre completo del usuario (opcional).
 * @property registeredAt Fecha de registro del usuario.
 * @property firstName Primer nombre del usuario.
 * @property lastName Apellido del usuario.
 * @property accessToken Token de acceso para autenticación.
 * @property expirationToken Fecha de expiración del token de acceso.
 * @property refreshToken Token para refrescar el token de acceso.
 * @property expiresRefresh Fecha de expiración del token de refresco.
 * @property serverUtcTime Hora UTC del servidor al momento de la respuesta.
 * @property group Grupo al que pertenece el usuario.
 * @property email Correo electrónico del usuario.
 */
@Serializable
data class UserApiModel(
    val id: Int = -1,
    val name: String? = null,
    @SerialName("registeredAt")
    val registeredAt: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("expiration_token")
    val expirationToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    @SerialName("expires_refresh")
    val expiresRefresh: String? = null,
    @SerialName("server_utc_time")
    val serverUtcTime: String? = null,
    @SerialName("group")
    val group: String? = null,
    val email: String? = null
) {
    /**
     * Convierte este modelo de datos remoto en un objeto de dominio [User].
     *
     * @param hashedPassword Contraseña hasheada (opcional, por defecto vacío).
     * @return Un objeto [User] con los datos mapeados y valores predeterminados.
     */
    fun toDomain(hashedPassword: String = ""): User {
        return User(
            uuid = if (id != -1) UUID.nameUUIDFromBytes(id.toString().toByteArray()) else UUID.nameUUIDFromBytes(email?.toByteArray() ?: UUID.randomUUID().toString().toByteArray()),
            name = "${firstName ?: ""} ${lastName ?: ""}".trim().ifEmpty {
                name ?: email?.split("@")?.first()?.split(".")?.first() ?: ""
            },
            email = email ?: "",
            hashedPassword = hashedPassword,
            creationDate = registeredAt ?: "",
            lastConnection = serverUtcTime ?: "",
            deviceId = generateDeviceId(),
            accessToken = accessToken ?: "",
            refreshToken = refreshToken ?: "",
            expirationToken = expirationToken ?: "",
            expiresRefresh = expiresRefresh ?: "",
            group = group ?: "default" // Establece grupo "default" si no se proporciona ninguno
        )
    }

    /**
     * Fusiona este objeto [UserApiModel] con otro, dando preferencia a los valores
     * no nulos del objeto [other]. Algunos campos de token siempre se conservan.
     *
     * @param other Otro objeto [UserApiModel] para fusionar.
     * @return Nuevo objeto [UserApiModel] con la fusión de datos.
     */
    fun mergeWith(other: UserApiModel): UserApiModel {
        return UserApiModel(
            id = if (other.id != -1) other.id else this.id,
            name = other.name ?: this.name,
            registeredAt = other.registeredAt ?: this.registeredAt,
            firstName = other.firstName ?: this.firstName,
            lastName = other.lastName ?: this.lastName,
            accessToken = this.accessToken, // Siempre conserva el token de la respuesta de login
            expirationToken = this.expirationToken,
            refreshToken = this.refreshToken,
            expiresRefresh = this.expiresRefresh,
            serverUtcTime = other.serverUtcTime ?: this.serverUtcTime,
            group = other.group ?: this.group,
            email = other.email ?: this.email
        )
    }

    /**
     * Genera un identificador de dispositivo aleatorio con el formato AAA999AAA999,
     * donde A son letras mayúsculas y 9 son dígitos numéricos.
     *
     * @return Identificador de dispositivo generado aleatoriamente.
     */
    private fun generateDeviceId(): String {
        val letters = ('A'..'Z').toList()
        val digits = ('0'..'9').toList()
        return buildString {
            repeat(3) { append(letters[Random.nextInt(letters.size)]) } // 3 letras mayúsculas
            repeat(3) { append(digits[Random.nextInt(digits.size)]) } // 3 dígitos
            repeat(3) { append(letters[Random.nextInt(letters.size)]) } // 3 letras mayúsculas
            repeat(3) { append(digits[Random.nextInt(digits.size)]) } // 3 dígitos
        }
    }

    companion object {
        /**
         * Crea un objeto [UserApiModel] a partir de un objeto de dominio [User].
         *
         * @param user Objeto de dominio [User] a convertir.
         * @return Instancia de [UserApiModel] con los datos mapeados desde [User].
         */
        fun fromDomain(user: User): UserApiModel {
            val id = try { UUID.fromString(user.uuid.toString()).hashCode() } catch (e: IllegalArgumentException) { -1 }

            val nameParts = user.name.trim().split(" ", limit = 2)
            val firstName = nameParts.getOrNull(0).let { if (it.isNullOrEmpty()) null else it }
            val lastName = nameParts.getOrNull(1).let { if (it.isNullOrEmpty()) null else it }

            return UserApiModel(
                id = id,
                name = user.name.ifEmpty { null },
                registeredAt = user.creationDate.ifEmpty { null },
                firstName = firstName,
                lastName = lastName,
                accessToken = user.accessToken.ifEmpty { null },
                expirationToken = user.expirationToken.ifEmpty { null },
                refreshToken = user.refreshToken.ifEmpty { null },
                expiresRefresh = user.expiresRefresh.ifEmpty { null },
                serverUtcTime = user.lastConnection.ifEmpty { null },
                group = user.group.ifEmpty { null },
                email = user.email.ifEmpty { null }
            )
        }
    }
}
