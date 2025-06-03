package cat.deim.asm_22.p2_patinfly.data.datasource.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import cat.deim.asm_22.p2_patinfly.domain.models.User
import java.util.UUID

/**
 * Representa una entidad de usuario en la base de datos Room, mapeada a la tabla 'user'.
 * Esta clase almacena los datos persistentes de un usuario, incluyendo información personal,
 * datos de autenticación (tokens) y metadatos como fechas de creación y conexión.
 * Proporciona métodos para convertir entre el modelo de dominio (User) y el modelo de datos (UserDTO).
 */
@Entity(tableName = "user")
data class UserDTO(
    @PrimaryKey val uuid: UUID,
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
) {
    /**
     * Convierte el objeto UserDTO (datos de la base de datos) a un objeto User de dominio.
     * Todos los campos, incluyendo datos de sesión (tokens y grupo), se copian directamente
     * desde la base de datos. Esto permite restaurar la sesión completa después de un reinicio.
     *
     * @return Un objeto User con los datos mapeados desde este UserDTO.
     */
    fun toDomain() = User(
        uuid = uuid,
        name = name,
        email = email,
        hashedPassword = hashedPassword,
        creationDate = creationDate,
        lastConnection = lastConnection,
        deviceId = deviceId,
        accessToken = accessToken,
        refreshToken = refreshToken,
        expirationToken = expirationToken,
        expiresRefresh = expiresRefresh,
        group = group
    )

    /**
     * Objeto compañero que contiene métodos estáticos para crear instancias de UserDTO.
     */
    companion object {
        /**
         * Convierte un objeto User de dominio a un UserDTO para almacenar en la base de datos.
         * Todos los campos, incluyendo datos de sesión (tokens y grupo), se persisten
         * para mantener la información completa del usuario, incluyendo tokens de autenticación.
         *
         * @param user El objeto User de dominio que se va a convertir.
         * @return Un nuevo objeto UserDTO con los datos mapeados desde el User.
         */
        fun fromDomain(user: User) = UserDTO(
            uuid = user.uuid,
            name = user.name,
            email = user.email,
            hashedPassword = user.hashedPassword,
            creationDate = user.creationDate,
            lastConnection = user.lastConnection,
            deviceId = user.deviceId,
            accessToken = user.accessToken,
            refreshToken = user.refreshToken,
            expirationToken = user.expirationToken,
            expiresRefresh = user.expiresRefresh,
            group = user.group
        )
    }
}