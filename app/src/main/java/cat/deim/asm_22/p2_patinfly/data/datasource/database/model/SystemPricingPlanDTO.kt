package cat.deim.asm_22.p2_patinfly.data.datasource.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import cat.deim.asm_22.p2_patinfly.domain.models.SystemPricingPlan
import cat.deim.asm_22.p2_patinfly.domain.models.DataPlanDomain
import com.google.gson.Gson

/**
 * Representa una entidad de plan de precios del sistema en la base de datos Room,
 * mapeada a la tabla 'systempricingplan'. Esta clase almacena información sobre
 * la versión, la última actualización, el tiempo de vida (TTL) y los datos del plan
 * en formato JSON, proporcionando métodos para convertir entre el modelo de dominio
 * [SystemPricingPlan] y el modelo de datos [SystemPricingPlanDTO].
 */
@Entity(tableName = "systempricingplan")
data class SystemPricingPlanDTO(
    @PrimaryKey val version: String,
    val lastUpdated: String,
    val ttl: Int,
    val dataJson: String
) {
    /**
     * Convierte el modelo de datos [SystemPricingPlanDTO] a un objeto del dominio [SystemPricingPlan].
     * Utiliza Gson para deserializar el campo [dataJson] en un objeto [DataPlanDomain],
     * que se incluye en el resultado.
     *
     * @return Objeto [SystemPricingPlan] que representa el plan de precios en el dominio de la aplicación.
     */
    fun toDomain(): SystemPricingPlan {
        val gson = Gson()
        val dataPlanDomain = gson.fromJson(dataJson, DataPlanDomain::class.java)
        return SystemPricingPlan(
            version = version,
            lastUpdated = lastUpdated,
            ttl = ttl,
            data = dataPlanDomain
        )
    }

    /**
     * Objeto compañero que contiene métodos estáticos para crear instancias de [SystemPricingPlanDTO].
     */
    companion object {
        /**
         * Convierte un objeto del dominio [SystemPricingPlan] a un modelo de datos [SystemPricingPlanDTO].
         * Utiliza Gson para serializar el campo data en un string JSON que se almacena en [dataJson].
         *
         * @param systemPricingPlan Objeto del dominio [SystemPricingPlan] que se desea convertir a modelo de datos.
         * @return [SystemPricingPlanDTO] que representa el plan de precios en el formato utilizado por la base de datos.
         */
        fun fromDomain(systemPricingPlan: SystemPricingPlan): SystemPricingPlanDTO {
            val gson = Gson()
            val dataJson = gson.toJson(systemPricingPlan.data)
            return SystemPricingPlanDTO(
                version = systemPricingPlan.version,
                lastUpdated = systemPricingPlan.lastUpdated,
                ttl = systemPricingPlan.ttl,
                dataJson = dataJson
            )
        }
    }
}