package cat.deim.asm_22.p2_patinfly.data.datasource.local

import android.annotation.SuppressLint
import android.content.Context
import cat.deim.asm_22.p2_patinfly.data.datasource.IBikeDataSource
import cat.deim.asm_22.p2_patinfly.data.datasource.local.model.BikeModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * Clase que implementa la fuente de datos local para las bicicletas.
 * Esta clase maneja el acceso y almacenamiento de los datos de bicicletas de manera local,
 * utilizando un archivo JSON almacenado en recursos y un mapa en memoria para gestionar los datos.
 */
class BikeLocalDataSource private constructor(): IBikeDataSource {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: BikeLocalDataSource? = null

        /**
         * Obtiene la instancia singleton de BikeLocalDataSource.
         *
         * Implementa el patrón Singleton con doble verificación para asegurar que
         * solo exista una instancia de esta clase y se inicialice una única vez.
         *
         * @param context Contexto de la aplicación para acceder a los recursos.
         * @return Instancia única de BikeLocalDataSource.
         */
        fun getInstance(context: Context): BikeLocalDataSource =
            instance ?: synchronized(this) {
                instance ?: BikeLocalDataSource().also {
                    instance = it
                    it.context = context
                    it.loadBikeData()
                }
            }
    }

    private var context: Context? = null
    private val bikesMap: MutableMap<String, BikeModel> = HashMap()

    /**
     * Carga los datos de bicicletas desde los recursos locales (archivo JSON).
     *
     * Esta función lee el archivo JSON localizado en `res/raw/bikes.json`,
     * lo parsea y guarda las bicicletas en un mapa en memoria para un acceso rápido.
     * En caso de error o archivo vacío, no realiza ninguna acción.
     */
    private fun loadBikeData() {
        context?.let { context ->
            try {
                val jsonData = AssetsProvider.getJsonDataFromRawAsset(context, "bikes")

                if (jsonData.isNullOrEmpty()) {
                    return
                }

                val bikes = parseJson(jsonData)
                if (bikes.isNullOrEmpty()) {
                    return
                }

                bikes.forEach { bikeModel ->
                    save(bikeModel)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Parsea el JSON recibido a una lista de objetos [BikeModel].
     *
     * El JSON debe contener un array bajo la clave `"bike"`.
     * Si el JSON no es válido o el parsing falla, devuelve null.
     *
     * @param json Cadena JSON que representa los datos de bicicletas.
     * @return Lista de objetos [BikeModel] o null si ocurre un error.
     */
    private fun parseJson(json: String): List<BikeModel>? {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
        return try {
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val bikesArray = jsonObject.getAsJsonArray("bike")
            val bikeListType = object : TypeToken<List<BikeModel>>() {}.type
            gson.fromJson(bikesArray, bikeListType)
        } catch (e: JsonSyntaxException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Guarda una bicicleta en el mapa de bicicletas.
     *
     * @param bikeModel El modelo de bicicleta a guardar.
     */
    private fun save(bikeModel: BikeModel) {
        try {
            bikesMap[bikeModel.uuid] = bikeModel
        } catch (e: Exception) {
            // Manejo de error opcional
        }
    }

    /**
     * Inserta una nueva bicicleta si no existe una con el mismo UUID.
     *
     * @param bikeModel El modelo de bicicleta a insertar.
     * @return true si la bicicleta fue insertada correctamente, false si ya existía.
     */
    override fun insert(bikeModel: BikeModel): Boolean {
        return if (!bikesMap.containsKey(bikeModel.uuid)) {
            bikesMap[bikeModel.uuid] = bikeModel
            true
        } else {
            false
        }
    }

    /**
     * Inserta o actualiza una bicicleta en el mapa de bicicletas.
     *
     * @param bikeModel El modelo de bicicleta a insertar o actualizar.
     * @return true siempre, indicando que la operación se realizó.
     */
    override fun insertOrUpdate(bikeModel: BikeModel): Boolean {
        bikesMap[bikeModel.uuid] = bikeModel
        return true
    }

    /**
     * Obtiene una bicicleta de forma arbitraria del mapa de bicicletas.
     *
     * @return Una bicicleta si existe alguna, o null si no hay bicicletas almacenadas.
     */
    override fun getBike(): BikeModel? {
        return bikesMap.values.firstOrNull()
    }

    /**
     * Obtiene una bicicleta por su UUID.
     *
     * @param uuid El UUID de la bicicleta a buscar.
     * @return La bicicleta correspondiente al UUID, o null si no se encuentra.
     */
    override fun getBikeById(uuid: String): BikeModel? {
        return bikesMap[uuid]
    }

    /**
     * Obtiene todas las bicicletas almacenadas.
     *
     * @return Una colección con todos los modelos de bicicletas almacenadas.
     */
    override fun getAll(): Collection<BikeModel> = bikesMap.values

    /**
     * Obtiene todas las bicicletas, con opción de filtrado por tipo.
     *
     * @param bikeTypeFilter Nombre del tipo de bicicleta para filtrar (opcional).
     * @return Colección con bicicletas que coinciden con el filtro, o todas si el filtro es nulo o vacío.
     */
    fun getAll(bikeTypeFilter: String? = null): Collection<BikeModel> {
        return if (bikeTypeFilter.isNullOrEmpty()) {
            bikesMap.values
        } else {
            bikesMap.values.filter { bikeModel ->
                bikeModel.bikeType.name.equals(bikeTypeFilter, ignoreCase = true)
            }
        }
    }

    /**
     * Actualiza una bicicleta en el mapa de bicicletas.
     *
     * @param bikeModel El modelo de bicicleta con los nuevos datos.
     * @return El modelo actualizado de bicicleta, o null si no existe la bicicleta con ese UUID.
     */
    override fun update(bikeModel: BikeModel): BikeModel? {
        return bikesMap[bikeModel.uuid]?.apply {
            bikesMap[bikeModel.uuid] = bikeModel
        }
    }

    /**
     * Elimina la primera bicicleta almacenada en el mapa.
     *
     * @return La bicicleta eliminada, o null si no hay bicicletas para eliminar.
     */
    override fun delete(): BikeModel? {
        return bikesMap.values.firstOrNull()?.also { bikesMap.remove(it.uuid) }
    }
}
