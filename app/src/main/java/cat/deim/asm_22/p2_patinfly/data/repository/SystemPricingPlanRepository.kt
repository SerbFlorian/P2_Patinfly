package cat.deim.asm_22.p2_patinfly.data.repository

import android.content.Context
import cat.deim.asm_22.p2_patinfly.data.datasource.database.AppDatabase
import cat.deim.asm_22.p2_patinfly.data.datasource.database.SystemPricingPlanDataSource
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.SystemPricingPlanDTO
import cat.deim.asm_22.p2_patinfly.data.datasource.local.SystemLocalPricingPlanDataSource
import cat.deim.asm_22.p2_patinfly.domain.models.SystemPricingPlan
import cat.deim.asm_22.p2_patinfly.domain.repository.ISystemPricingPlanRepository

/**
 * Implementación del repositorio para la gestión de planes de precios del sistema.
 * Este repositorio abstrae la interacción con las distintas fuentes de datos (base de datos local y almacenamiento local).
 *
 * Proporciona métodos para insertar, obtener, actualizar y eliminar planes de precios.
 *
 * @property systemPricingPlanDataSource Fuente de datos principal (generalmente base de datos Room).
 * @property context Contexto de la aplicación para acceder a recursos o data sources locales.
 */
class SystemPricingPlanRepository(
    private val systemPricingPlanDataSource: SystemPricingPlanDataSource,
    private val context: Context
) : ISystemPricingPlanRepository {

    companion object {
        /**
         * Metodo factoría para crear una instancia configurada con la fuente de datos local de base de datos.
         * Facilita la creación del repositorio sin necesidad de pasar la fuente de datos externamente.
         *
         * @param context Contexto de la aplicación, requerido para inicializar la base de datos.
         * @return Instancia de [SystemPricingPlanRepository] configurada con el data source local.
         */
        fun create(context: Context): ISystemPricingPlanRepository {
            val dataSource = AppDatabase.getDatabase(context).systemPricingPlanDataSource()
            return SystemPricingPlanRepository(dataSource, context)
        }
    }

    /**
     * Inserta un nuevo plan de precios en la fuente de datos.
     *
     * @param systemPricingPlan Plan de precios del dominio a guardar.
     * @return [Boolean] Indica éxito (true) o fallo (false) en la operación.
     */
    override suspend fun setPricingPlan(systemPricingPlan: SystemPricingPlan): Boolean {
        return try {
            // Convierte el modelo de dominio a DTO para persistencia.
            systemPricingPlanDataSource.save(SystemPricingPlanDTO.fromDomain(systemPricingPlan))
            true
        } catch (e: Exception) {
            // Captura cualquier error de la operación y retorna falso.
            false
        }
    }

    /**
     * Obtiene el plan de precios actualmente almacenado localmente.
     * Se utiliza principalmente para recuperar el plan sin especificar versión.
     *
     * @return Instancia de [SystemPricingPlan] si existe, o null si no hay ninguno guardado.
     */
    override suspend fun getPricingPlan(): SystemPricingPlan? {
        val pricingPlanDTO = SystemLocalPricingPlanDataSource.getInstance(context).getPlan()
        return pricingPlanDTO?.toDomain()
    }

    /**
     * Obtiene un plan de precios específico según su versión.
     * Si no existe localmente, intenta obtenerlo desde la fuente local secundaria,
     * guardándolo luego en la base de datos principal para futuras consultas.
     *
     * @param version Versión específica del plan que se desea obtener.
     * @return Instancia de [SystemPricingPlan] correspondiente a la versión o null si no se encuentra.
     */
    suspend fun getPricingPlan(version: String): SystemPricingPlan? {
        // Intentamos obtener el plan de precios por versión de la base de datos principal.
        val pricingPlanDTO = systemPricingPlanDataSource.getByVersion(version)?.toDomain()
        if(pricingPlanDTO != null) return pricingPlanDTO

        // Si no está, se intenta obtener de la fuente local secundaria.
        val remotePricingPlan = SystemLocalPricingPlanDataSource.getInstance(context).getPlan()?.toDomain()
        if(remotePricingPlan != null){
            // Guardamos la versión remota en la base de datos principal para sincronización.
            systemPricingPlanDataSource.save(SystemPricingPlanDTO.fromDomain(remotePricingPlan))
            return remotePricingPlan
        }
        // No se encontró el plan solicitado.
        return null
    }

    /**
     * Actualiza un plan de precios existente en la base de datos.
     *
     * @param systemPricingPlan Plan de precios actualizado.
     * @return El plan actualizado si la operación fue exitosa, o null si falló.
     */
    override suspend fun updatePricingPlan(systemPricingPlan: SystemPricingPlan): SystemPricingPlan? {
        return try {
            systemPricingPlanDataSource.update(SystemPricingPlanDTO.fromDomain(systemPricingPlan))
            systemPricingPlan
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Elimina el primer plan de precios almacenado en la fuente de datos.
     *
     * @return El plan eliminado si existía, o null si no se pudo eliminar ninguno.
     */
    override suspend fun deletePricingPlan(): SystemPricingPlan? {
        // Se obtiene el primer plan para eliminarlo (asume que solo hay uno relevante o se quiere eliminar el primero).
        val pricingPlanDTO = systemPricingPlanDataSource.getAll().firstOrNull()
        return pricingPlanDTO?.toDomain()?.apply {
            // Se elimina el plan encontrado.
            systemPricingPlanDataSource.delete(pricingPlanDTO)
        }
    }
}
