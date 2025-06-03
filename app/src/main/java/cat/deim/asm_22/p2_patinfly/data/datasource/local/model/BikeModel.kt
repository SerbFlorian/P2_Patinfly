package cat.deim.asm_22.p2_patinfly.data.datasource.local.model

import com.google.gson.annotations.SerializedName
import cat.deim.asm_22.p2_patinfly.domain.models.Bike

/**
 * Modelo de datos que representa una bicicleta.
 * Este modelo se utiliza para mapear los datos de una bicicleta desde una fuente externa (como un JSON)
 * hacia el dominio de la aplicación.
 */
data class BikeModel(
    /** Identificador único de la bicicleta */
    @SerializedName("uuid") val uuid: String,

    /** Nombre de la bicicleta */
    @SerializedName("name") val name: String,

    /** Tipo de bicicleta representado mediante otro modelo */
    @SerializedName("bike_type") val bikeType: BikeTypeModel,

    /** Fecha de creación de la bicicleta */
    @SerializedName("creation_date") val creationDate: String,

    /** Fecha de la última revisión o mantenimiento, puede ser nula */
    @SerializedName("last_maintenance_date") val lastMaintenanceDate: String?,

    /** Indica si la bicicleta está actualmente en mantenimiento */
    @SerializedName("in_maintenance") val inMaintenance: Boolean,

    /** Indica si la bicicleta está activa */
    @SerializedName("is_active") val isActive: Boolean,

    /** Indica si la bicicleta ha sido eliminada lógicamente */
    @SerializedName("is_deleted") val isDeleted: Boolean,

    /** Nivel de batería actual de la bicicleta (si aplica) */
    @SerializedName("battery_level") val batteryLevel: Int,

    /** Cantidad total de metros recorridos por la bicicleta */
    @SerializedName("meters") val meters: Int,

    /** Indica si la bicicleta está alquilada en este momento */
    @SerializedName("is_rented") val isRented: Boolean,

    /** Nombre del tipo de bicicleta como texto plano */
    @SerializedName("bike_type_name") val bikeTypeName: String
) {

    /**
     * Convierte este modelo de datos a un objeto del dominio [Bike].
     *
     * @return Objeto [Bike] correspondiente a este modelo de datos.
     */
    fun toDomain(): Bike {
        return Bike(
            uuid = uuid,
            name = name,
            bikeType = bikeType.toDomain(),
            creationDate = creationDate,
            lastMaintenanceDate = lastMaintenanceDate,
            inMaintenance = inMaintenance,
            isActive = isActive,
            isDeleted = isDeleted,
            batteryLevel = batteryLevel,
            meters = meters,
            isRented = isRented,
            lat = null, // No disponible en el modelo local
            lon = null, // No disponible en el modelo local
            isReserved = false, // Valor por defecto
            rentalUris = listOf("uri1", "uri2", "uri3").toString(), // Simulado
            groupCourse = null,
            bikeTypeName = bikeTypeName
        )
    }

    companion object {
        /**
         * Crea un modelo de datos [BikeModel] a partir de un objeto del dominio [Bike].
         *
         * @param bike Objeto del dominio que se desea transformar.
         * @return Objeto [BikeModel] que representa los mismos datos en el formato del modelo de datos.
         */
        fun fromDomain(bike: Bike): BikeModel {
            return BikeModel(
                uuid = bike.uuid,
                name = bike.name,
                bikeType = BikeTypeModel.fromDomain(bike.bikeType),
                creationDate = bike.creationDate,
                lastMaintenanceDate = bike.lastMaintenanceDate,
                inMaintenance = bike.inMaintenance,
                isActive = bike.isActive,
                isDeleted = bike.isDeleted,
                batteryLevel = bike.batteryLevel,
                meters = bike.meters,
                isRented = bike.isRented,
                bikeTypeName = bike.bikeTypeName
            )
        }
    }
}
