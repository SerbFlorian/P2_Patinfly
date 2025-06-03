package cat.deim.asm_22.p2_patinfly.data.datasource

import cat.deim.asm_22.p2_patinfly.data.datasource.local.model.SystemPricingPlanModel

/**
 * Interfaz para la fuente de datos de planes tarifarios del sistema,
 * que define operaciones básicas de inserción, actualización, consulta y eliminación.
 */
interface ISystemPricingPlanDataSource {

    /**
     * Inserta un nuevo [SystemPricingPlanModel] en la fuente de datos.
     *
     * @param systemPricingPlanModel El modelo de plan tarifario a insertar.
     * @return `true` si la inserción fue exitosa, `false` en caso contrario.
     */
    fun insert(systemPricingPlanModel: SystemPricingPlanModel): Boolean

    /**
     * Inserta un nuevo [SystemPricingPlanModel] o actualiza uno existente en la fuente de datos.
     *
     * @param systemPricingPlanModel El modelo de plan tarifario a insertar o actualizar.
     * @return `true` si la operación fue exitosa, `false` en caso contrario.
     */
    fun insertOrUpdate(systemPricingPlanModel: SystemPricingPlanModel): Boolean

    /**
     * Obtiene un plan tarifario cualquiera de la fuente de datos.
     *
     * @return Un [SystemPricingPlanModel] si existe algún plan almacenado, o `null` si no hay ninguno.
     */
    fun getPlan(): SystemPricingPlanModel?

    /**
     * Obtiene un plan tarifario por su identificador único.
     *
     * @param planId Identificador único del plan tarifario.
     * @return El [SystemPricingPlanModel] correspondiente al identificador, o `null` si no se encuentra.
     */
    fun getPlanById(planId: String): SystemPricingPlanModel?

    /**
     * Actualiza la información de un plan tarifario existente.
     *
     * @param systemPricingPlanModel El modelo de plan tarifario con los datos actualizados.
     * @return El [SystemPricingPlanModel] actualizado si la operación fue exitosa, o `null` en caso contrario.
     */
    fun update(systemPricingPlanModel: SystemPricingPlanModel): SystemPricingPlanModel?

    /**
     * Elimina un plan tarifario de la fuente de datos.
     *
     * @return El [SystemPricingPlanModel] eliminado si la operación fue exitosa, o `null` si no se pudo eliminar.
     */
    fun delete(): SystemPricingPlanModel?

    /**
     * Obtiene todos los planes tarifarios almacenados en la fuente de datos.
     *
     * @return Una colección con todos los modelos de planes tarifarios disponibles.
     */
    fun getAll(): Collection<SystemPricingPlanModel>
}
