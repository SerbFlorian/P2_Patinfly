package cat.deim.asm_22.p2_patinfly.data.datasource.remote.status

import cat.deim.asm_22.p2_patinfly.domain.models.ServerStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Modelo de datos que representa el estado del servidor recibido desde la API remota.
 *
 * Esta clase es usada para deserializar la respuesta JSON que contiene información
 * sobre la versión, la compilación, la fecha de actualización y el nombre del servidor.
 *
 * @property version Versión actual del servidor.
 * @property build Identificador o número de compilación del servidor.
 * @property update Fecha o información sobre la última actualización del servidor.
 * @property name Nombre identificativo del servidor.
 */
@Serializable
data class ServerStatusApiModel(
    @SerialName("version") val version: String,
    @SerialName("build") val build: String,
    @SerialName("update") val update: String,
    @SerialName("name") val name: String
) {
    /**
     * Convierte este modelo de datos de la API en el modelo de dominio [ServerStatus].
     *
     * Esta función permite transformar los datos que vienen de la capa remota
     * para su uso dentro de la lógica de negocio o la capa de dominio.
     *
     * @return Un objeto [ServerStatus] con los datos equivalentes.
     */
    fun toDomain(): ServerStatus {
        return ServerStatus(
            version = version,
            build = build,
            update = update,
            name = name
        )
    }
}
