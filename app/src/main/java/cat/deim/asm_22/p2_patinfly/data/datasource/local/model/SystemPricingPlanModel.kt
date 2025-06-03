package cat.deim.asm_22.p2_patinfly.data.datasource.local.model

import cat.deim.asm_22.p2_patinfly.domain.models.SystemPricingPlan
import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa un plan de precios del sistema, con información adicional
 * como la fecha de la última actualización, tiempo de vida (TTL), versión y los datos de planes asociados.
 * Este modelo se utiliza para mapear los datos del sistema de precios desde una fuente externa (como JSON)
 * hacia el dominio de la aplicación.
 *
 * @property lastUpdated Fecha de la última actualización de los datos, en formato String.
 * @property ttl Tiempo de vida (Time To Live) en segundos para los datos.
 * @property version Versión del sistema de precios.
 * @property data Datos que contienen los planes de precios.
 */
data class SystemPricingPlanModel(
    @SerializedName("last_updated") val lastUpdated: String,
    val ttl: Int,
    val version: String,
    @SerializedName("data") val data: DataPlan
) {

    /**
     * Convierte el modelo de datos [SystemPricingPlanModel] a un objeto del dominio [SystemPricingPlan].
     *
     * @return Instancia de [SystemPricingPlan] que representa el sistema de precios en el dominio de la aplicación.
     */
    fun toDomain(): SystemPricingPlan {
        return SystemPricingPlan(
            lastUpdated = lastUpdated,
            ttl = ttl,
            version = version,
            data = data.toDomain()
        )
    }

    companion object {
        /**
         * Convierte un objeto del dominio [SystemPricingPlan] a un modelo de datos [SystemPricingPlanModel].
         *
         * @param domain Instancia de [SystemPricingPlan] que se desea convertir a modelo de datos.
         * @return Instancia de [SystemPricingPlanModel] que representa el sistema de precios en el formato de la fuente de datos.
         */
        fun fromDomain(domain: SystemPricingPlan): SystemPricingPlanModel {
            return SystemPricingPlanModel(
                lastUpdated = domain.lastUpdated,
                ttl = domain.ttl,
                version = domain.version,
                data = DataPlan.fromDomain(domain.data)
            )
        }
    }
}
