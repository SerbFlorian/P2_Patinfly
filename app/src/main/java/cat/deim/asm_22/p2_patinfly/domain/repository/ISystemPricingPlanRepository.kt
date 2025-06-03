package cat.deim.asm_22.p2_patinfly.domain.repository

import cat.deim.asm_22.p2_patinfly.domain.models.SystemPricingPlan

/**
 * Interfaz que define las operaciones para manejar planes de precios del sistema.
 */
interface ISystemPricingPlanRepository {

    /**
     * Guarda un plan de precios en el repositorio.
     *
     * @param systemPricingPlan Objeto [SystemPricingPlan] a guardar.
     * @return `true` si la operación fue exitosa, `false` en caso contrario.
     */
    suspend fun setPricingPlan(systemPricingPlan: SystemPricingPlan): Boolean

    /**
     * Obtiene el plan de precios almacenado.
     *
     * @return El plan de precios si existe, o `null` si no se encontró ninguno.
     */
    suspend fun getPricingPlan(): SystemPricingPlan?

    /**
     * Actualiza el plan de precios existente.
     *
     * @param systemPricingPlan Objeto [SystemPricingPlan] con los datos actualizados.
     * @return El plan de precios actualizado, o `null` si la actualización falló.
     */
    suspend fun updatePricingPlan(systemPricingPlan: SystemPricingPlan): SystemPricingPlan?

    /**
     * Elimina el plan de precios almacenado.
     *
     * @return El plan de precios eliminado, o `null` si no se pudo eliminar ninguno.
     */
    suspend fun deletePricingPlan(): SystemPricingPlan?
}
