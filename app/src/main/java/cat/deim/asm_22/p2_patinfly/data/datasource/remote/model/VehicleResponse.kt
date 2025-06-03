package cat.deim.asm_22.p2_patinfly.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Modelo que representa la respuesta de la API para la lista de vehículos.
 *
 * @property vehicles Lista de vehículos obtenidos de la API, representados por objetos [BikeApiModel].
 */
@Serializable
data class VehiclesResponse(
    @SerialName("vehicles") val vehicles: List<BikeApiModel>
)
