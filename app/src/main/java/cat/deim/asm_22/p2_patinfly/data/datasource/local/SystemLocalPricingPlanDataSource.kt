package cat.deim.asm_22.p2_patinfly.data.datasource.local

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import cat.deim.asm_22.p2_patinfly.data.datasource.ISystemPricingPlanDataSource
import cat.deim.asm_22.p2_patinfly.data.datasource.local.model.SystemPricingPlanModel
import com.google.gson.GsonBuilder
import java.util.concurrent.Executors

/**
 * Implementación de ISystemPricingPlanDataSource que utiliza un archivo JSON en la carpeta
 * de recursos raw para almacenar los planes de precios del sistema.
 */
class SystemLocalPricingPlanDataSource : ISystemPricingPlanDataSource {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: SystemLocalPricingPlanDataSource? = null

        private const val TAG = "SystemPricingPlanSource"

        /**
         * Obtiene una instancia singleton de SystemLocalPricingPlanDataSource.
         * Si la instancia no existe, se crea y se carga la información de los planes.
         *
         * @param context Contexto de la aplicación.
         * @return Instancia singleton de SystemLocalPricingPlanDataSource.
         */
        fun getInstance(context: Context): SystemLocalPricingPlanDataSource =
            instance ?: synchronized(this) {
                instance ?: SystemLocalPricingPlanDataSource().apply {
                    this.context = context
                    loadPricingPlanData()
                }.also { instance = it }
            }
    }

    /** Contexto de la aplicación, inicializado en getInstance */
    private lateinit var context: Context

    /** Mapa que almacena los planes de precios con clave planId */
    private val pricingPlansMap: MutableMap<String, SystemPricingPlanModel> = HashMap()

    /**
     * Carga los datos de los planes de precios desde un archivo JSON en la carpeta raw de recursos,
     * y los guarda en el mapa local de planes.
     * Se ejecuta en un hilo separado para no bloquear la interfaz.
     */
    private fun loadPricingPlanData() {
        Executors.newSingleThreadExecutor().execute {
            try {
                val jsonData = AssetsProvider.getJsonDataFromRawAsset(context, "system_pricing_plans")
                if (jsonData.isNullOrEmpty()) {
                    Log.e(TAG, "No se pudo cargar el archivo JSON.")
                    return@execute
                }
                Log.d(TAG, "JSON cargado: $jsonData")

                val pricingPlan = parseJson(jsonData)
                pricingPlan?.data?.plans?.forEach { plan ->
                    save(pricingPlan.copy(data = pricingPlan.data.copy(plans = listOf(plan))))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar datos de planes de precios: ${e.message}")
            }
        }
    }

    /**
     * Parsea un JSON a un objeto SystemPricingPlanModel utilizando Gson.
     *
     * @param json String con contenido JSON a parsear.
     * @return Objeto SystemPricingPlanModel o null si ocurre un error.
     */
    private fun parseJson(json: String): SystemPricingPlanModel? {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssz")
            .create()
        return try {
            gson.fromJson(json, SystemPricingPlanModel::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al parsear JSON: ${e.message}")
            null
        }
    }

    /**
     * Guarda un plan de precios en el mapa local, utilizando el planId como clave.
     *
     * @param plan Plan de precios a guardar.
     */
    private fun save(plan: SystemPricingPlanModel) {
        pricingPlansMap[plan.data.plans.first().planId] = plan
    }

    /**
     * Inserta un nuevo plan de precios si no existe uno con el mismo ID.
     *
     * @param systemPricingPlanModel Plan a insertar.
     * @return true si se insertó correctamente, false si ya existía un plan con el mismo ID.
     */
    override fun insert(systemPricingPlanModel: SystemPricingPlanModel): Boolean {
        val planId = systemPricingPlanModel.data.plans.first().planId
        if (pricingPlansMap.containsKey(planId)) {
            Log.d(TAG, "El plan con ID $planId ya existe.")
            return false
        }
        pricingPlansMap[planId] = systemPricingPlanModel
        Log.d(TAG, "Plan de precios insertado: $systemPricingPlanModel")
        return true
    }

    /**
     * Inserta o actualiza un plan de precios en el mapa.
     * Si el plan ya existe, lo actualiza.
     *
     * @param systemPricingPlanModel Plan a insertar o actualizar.
     * @return true siempre (la operación se considera exitosa).
     */
    override fun insertOrUpdate(systemPricingPlanModel: SystemPricingPlanModel): Boolean {
        val planId = systemPricingPlanModel.data.plans.first().planId
        pricingPlansMap[planId] = systemPricingPlanModel
        return true
    }

    /**
     * Obtiene el primer plan de precios disponible en el mapa.
     *
     * @return Primer plan de precios o null si no hay ninguno.
     */
    override fun getPlan(): SystemPricingPlanModel? {
        val plan = pricingPlansMap.values.firstOrNull()
        Log.d(TAG, "Obteniendo primer plan de precios: $plan")
        return plan
    }

    /**
     * Obtiene un plan de precios dado su ID.
     *
     * @param planId ID del plan.
     * @return Plan de precios con ese ID o null si no existe.
     */
    override fun getPlanById(planId: String): SystemPricingPlanModel? {
        return pricingPlansMap[planId]
    }

    /**
     * Actualiza un plan de precios existente.
     *
     * @param systemPricingPlanModel Plan actualizado.
     * @return El plan actualizado, o null si no existía un plan con ese ID.
     */
    override fun update(systemPricingPlanModel: SystemPricingPlanModel): SystemPricingPlanModel? {
        val planId = systemPricingPlanModel.data.plans.first().planId
        return if (pricingPlansMap.containsKey(planId)) {
            pricingPlansMap[planId] = systemPricingPlanModel
            systemPricingPlanModel
        } else {
            null
        }
    }

    /**
     * Elimina el primer plan de precios disponible en el mapa.
     *
     * @return El plan eliminado o null si no hay planes.
     */
    override fun delete(): SystemPricingPlanModel? {
        val firstPlan = pricingPlansMap.values.firstOrNull()
        if (firstPlan != null) {
            pricingPlansMap.remove(firstPlan.data.plans.first().planId)
        }
        return firstPlan
    }

    /**
     * Obtiene todos los planes de precios almacenados.
     *
     * @return Colección de todos los planes de precios.
     */
    override fun getAll(): Collection<SystemPricingPlanModel> {
        return pricingPlansMap.values.toList()
    }
}
