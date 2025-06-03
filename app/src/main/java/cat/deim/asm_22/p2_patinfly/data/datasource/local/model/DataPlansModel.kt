package cat.deim.asm_22.p2_patinfly.data.datasource.local.model

import cat.deim.asm_22.p2_patinfly.domain.models.DataPlanDomain
import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa un conjunto de planes en el sistema.
 * Este modelo es utilizado para mapear los datos de los planes desde una fuente de datos externa
 * (como JSON) hacia el dominio de la aplicación.
 *
 * @property plans Lista de objetos [Plan] que representan los planes disponibles.
 */
data class DataPlan(
    @SerializedName("plans") val plans: List<Plan>
) {

    /**
     * Convierte el modelo de datos [DataPlan] a un objeto del dominio [DataPlanDomain].
     *
     * @return Objeto [DataPlanDomain] que representa el conjunto de planes en el dominio de la aplicación.
     */
    fun toDomain(): DataPlanDomain {
        return DataPlanDomain(
            plans = plans.map { it.toDomain() }
        )
    }

    companion object {
        /**
         * Convierte un objeto del dominio [DataPlanDomain] a un modelo de datos [DataPlan].
         *
         * @param domain Objeto del dominio [DataPlanDomain] que se desea convertir.
         * @return Instancia de [DataPlan] correspondiente al modelo de datos utilizado por la fuente de datos.
         */
        fun fromDomain(domain: DataPlanDomain): DataPlan {
            return DataPlan(
                plans = domain.plans.map { Plan.fromDomain(it) }
            )
        }
    }
}
