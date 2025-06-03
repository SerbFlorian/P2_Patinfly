package cat.deim.asm_22.p2_patinfly.data.datasource.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import cat.deim.asm_22.p2_patinfly.domain.models.BikeType

/**
 * Representa una entidad de bicicleta en la base de datos Room, mapeada a la tabla 'bike'.
 * Esta clase contiene los datos persistentes de una bicicleta y proporciona métodos para
 * convertir entre el modelo de dominio (Bike) y el modelo de datos (BikeDTO).
 */
@Entity(tableName = "bike")
data class BikeDTO(
    @PrimaryKey val uuid: String,
    val name: String,
    val bikeType: BikeType,
    val bikeTypeName: String,
    val creationDate: String,
    val lastMaintenanceDate: String?,
    val inMaintenance: Boolean,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val batteryLevel: Int,
    val meters: Int,
    val lat: Double?,
    val lon: Double?,
    var isRented: Boolean
) {
    /**
     * Convierte el objeto BikeDTO a un objeto Bike del dominio.
     *
     * @return Un objeto Bike con los datos mapeados desde este BikeDTO.
     */
    fun toDomain(): Bike {
        return Bike(
            uuid = uuid,
            name = name,
            bikeTypeName = bikeTypeName,
            bikeType = bikeType,
            creationDate = creationDate,
            lastMaintenanceDate = lastMaintenanceDate,
            inMaintenance = inMaintenance,
            isActive = isActive,
            isDeleted = isDeleted,
            batteryLevel = batteryLevel,
            meters = meters,
            isRented = isRented,
            lat = lat,
            lon = lon,
            isReserved = false,
            rentalUris = listOf("uri1", "uri2", "uri3").toString(),
            groupCourse = null
        )
    }

    /**
     * Objeto compañero que contiene métodos estáticos para crear instancias de BikeDTO.
     */
    companion object {
        /**
         * Convierte un objeto Bike del dominio a un objeto BikeDTO.
         *
         * @param bike El objeto Bike del dominio que se va a convertir.
         * @return Un nuevo objeto BikeDTO con los datos mapeados desde el Bike.
         */
        fun fromDomain(bike: Bike): BikeDTO {
            return BikeDTO(
                uuid = bike.uuid,
                name = bike.name,
                bikeTypeName = bike.bikeTypeName,
                bikeType = bike.bikeType,
                creationDate = bike.creationDate,
                lastMaintenanceDate = bike.lastMaintenanceDate,
                inMaintenance = bike.inMaintenance,
                isActive = bike.isActive,
                isDeleted = bike.isDeleted,
                batteryLevel = bike.batteryLevel,
                meters = bike.meters,
                isRented = bike.isRented,
                lat = bike.lat,
                lon = bike.lon
            )
        }
    }
}