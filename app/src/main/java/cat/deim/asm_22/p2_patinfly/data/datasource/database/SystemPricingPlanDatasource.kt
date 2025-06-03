package cat.deim.asm_22.p2_patinfly.data.datasource.database

import androidx.room.*
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.SystemPricingPlanDTO

/**
 * Interfaz DAO (Data Access Object) para gestionar las operaciones
 * de acceso a la base de datos relacionadas con los planes de precios del sistema.
 */
@Dao
interface SystemPricingPlanDataSource {

    /**
     * Guarda un plan de precios en la base de datos.
     * Si ya existe un plan con la misma versión, lo reemplaza.
     *
     * @param plan objeto SystemPricingPlanDTO que se desea guardar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(plan: SystemPricingPlanDTO)

    /**
     * Actualiza un plan de precios existente en la base de datos.
     *
     * @param plan objeto SystemPricingPlanDTO con los nuevos datos
     */
    @Update
    suspend fun update(plan: SystemPricingPlanDTO)

    /**
     * Recupera un plan de precios a partir de su versión.
     *
     * @param version cadena que identifica la versión del plan
     * @return el plan de precios correspondiente o null si no se encuentra
     */
    @Query("SELECT * FROM systempricingplan WHERE version = :version")
    suspend fun getByVersion(version: String): SystemPricingPlanDTO?

    /**
     * Recupera todos los planes de precios almacenados en la base de datos.
     *
     * @return lista de todos los planes de precios
     */
    @Query("SELECT * FROM systempricingplan")
    suspend fun getAll(): List<SystemPricingPlanDTO>

    /**
     * Elimina un plan de precios de la base de datos.
     *
     * @param plan objeto SystemPricingPlanDTO que se desea eliminar
     */
    @Delete
    suspend fun delete(plan: SystemPricingPlanDTO)
}
