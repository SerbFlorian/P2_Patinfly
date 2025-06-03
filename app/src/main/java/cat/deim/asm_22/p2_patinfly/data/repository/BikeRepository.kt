package cat.deim.asm_22.p2_patinfly.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import cat.deim.asm_22.p2_patinfly.data.datasource.database.AppDatabase
import cat.deim.asm_22.p2_patinfly.data.datasource.database.BikeDatasource
import cat.deim.asm_22.p2_patinfly.data.datasource.database.model.BikeDTO
import cat.deim.asm_22.p2_patinfly.data.datasource.local.BikeLocalDataSource
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.BikeRemoteDataSource
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.network.RetrofitBike
import cat.deim.asm_22.p2_patinfly.data.repository.UserRepository.Companion.authToken
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import cat.deim.asm_22.p2_patinfly.domain.models.ServerStatus
import cat.deim.asm_22.p2_patinfly.domain.repository.IBikeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Clase repositorio responsable de gestionar los datos de las bicicletas.
 * Actúa como intermediario entre la base de datos local (Room) y la API remota,
 * proporcionando una única fuente de verdad y sincronizando datos cuando es necesario.
 *
 * @param bikeDatasource Fuente de datos para operaciones en la base de datos local.
 * @param localDataSource Fuente de datos local para caché o preferencias.
 * @param remoteDataSource Fuente de datos remota para la API.
 * @param context Contexto de la aplicación, usado principalmente para verificar la conectividad.
 */
class BikeRepository(
    private val bikeDatasource: BikeDatasource,
    private val localDataSource: BikeLocalDataSource,
    private val remoteDataSource: BikeRemoteDataSource,
    private val context: Context
) : IBikeRepository {

    companion object {

        /**
         * Metodo de fábrica para crear una instancia de BikeRepository,
         * inicializando todas las fuentes de datos necesarias y la configuración de Retrofit.
         *
         * @param context Contexto de la aplicación.
         * @return Instancia de IBikeRepository.
         */
        fun create(context: Context): IBikeRepository {
            val remote = BikeRemoteDataSource.getInstance()
            val local = BikeLocalDataSource.getInstance(context)
            val db = AppDatabase.getDatabase(context).bikeDataSource()
            RetrofitBike.initialize { authToken }
            return BikeRepository(db, local, remote, context)
        }
    }

    /**
     * Obtiene el token de autenticación actual utilizado para las llamadas a la API.
     * Útil para depuración y validación del estado de autenticación.
     *
     * @return Cadena con el token de autenticación o null si no existe.
     */
    private fun getAuthToken(): String? {
        val token = authToken
        Log.d("BikeRepository", "Token recuperado: ${token?.take(10)}...")
        return token
    }

    /**
     * Verifica si el dispositivo tiene una conexión de red activa.
     *
     * @return true si la red está disponible y conectada; false en caso contrario.
     */
    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }

    /**
     * Guarda un objeto bicicleta en la fuente de datos local.
     *
     * @param bike Modelo de dominio de la bicicleta a guardar.
     * @return true si la operación de guardado fue exitosa; false en caso contrario.
     */
    override suspend fun setBike(bike: Bike): Boolean = try {
        bikeDatasource.save(BikeDTO.fromDomain(bike))
        Log.d("BikeRepository", "Successfully saved bike: UUID=${bike.uuid}, Name='${bike.name}'")
        true
    } catch (e: Exception) {
        Log.e("BikeRepository", "Error saving bike: ${e.message}", e)
        false
    }

    /**
     * Recupera la primera bicicleta de la fuente de datos local.
     *
     * @return Objeto de dominio Bike si se encuentra; null en caso contrario.
     */
    override suspend fun getBike(): Bike? {
        val bike = bikeDatasource.getBike()?.toDomain()
        return if (bike != null) {
            Log.d("BikeRepository", "Found bike in local data source: '${bike.name}'")
            bike
        } else {
            Log.d("BikeRepository", "No bike found in local data source")
            null
        }
    }

    /**
     * Recupera una bicicleta por su UUID, consultando primero la base de datos local
     * y luego la API remota si hay red y token disponibles.
     *
     * @param uuid Identificador único de la bicicleta.
     * @return Objeto de dominio Bike si se encuentra; null en caso contrario.
     */
    override suspend fun getBike(uuid: String): Bike? = withContext(Dispatchers.IO) {
        try {
            val localBike = bikeDatasource.getByUUID(uuid)?.toDomain()
            if (localBike != null) {
                Log.d("BikeRepository", "Found bike $uuid in database: Name='${localBike.name}', Active=${localBike.isActive}")
                return@withContext localBike
            }

            if (isNetworkAvailable() && !getAuthToken().isNullOrEmpty()) {
                Log.d("BikeRepository", "No local bike found for $uuid. Fetching from API.")
                val token = getAuthToken() ?: return@withContext null
                val bikeFromApi = remoteDataSource.getBikeById(uuid, token)?.toDomain()
                if (bikeFromApi != null) {
                    Log.d("BikeRepository", "Fetched bike $uuid from API: Name='${bikeFromApi.name}', Active=${bikeFromApi.isActive}")
                    bikeDatasource.save(BikeDTO.fromDomain(bikeFromApi))
                    return@withContext bikeFromApi
                } else {
                    Log.d("BikeRepository", "Bike $uuid not found in API")
                    return@withContext null
                }
            } else {
                Log.d("BikeRepository", "No network or token to fetch bike $uuid")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e("BikeRepository", "Error fetching bike $uuid: ${e.message}", e)
            null
        }
    }

    /**
     * Actualiza una bicicleta existente en la fuente de datos local.
     *
     * @param bike Objeto de dominio Bike a actualizar.
     * @return Objeto Bike actualizado si la operación fue exitosa; null en caso contrario.
     */
    override suspend fun updateBike(bike: Bike): Bike? = try {
        setBike(bike)
        Log.d("BikeRepository", "Successfully updated bike: UUID=${bike.uuid}, Name='${bike.name}'")
        bike
    } catch (e: Exception) {
        Log.e("BikeRepository", "Error updating bike: ${e.message}", e)
        null
    }

    /**
     * Elimina la primera bicicleta encontrada en la fuente de datos local.
     *
     * @return Objeto Bike eliminado si existía; null si no había ninguna bicicleta para eliminar.
     */
    override suspend fun deleteBike(): Bike? {
        val bike = getBike()
        bike?.let {
            bikeDatasource.delete(BikeDTO.fromDomain(it))
            Log.d("BikeRepository", "Successfully deleted bike: UUID=${it.uuid}, Name='${it.name}'")
        } ?: run {
            Log.d("BikeRepository", "No bike to delete in local data source")
        }
        return bike
    }

    /**
     * Obtiene todas las bicicletas de la base de datos local, recurriendo a la API remota si no se encuentran localmente.
     * Filtra las bicicletas que están en mantenimiento.
     *
     * @return Colección de bicicletas disponibles.
     */
    override suspend fun getAll(): Collection<Bike> = withContext(Dispatchers.IO) {
        Log.d("BikeRepository", "Starting getAll() - Checking local data first")
        try {
            val local = bikeDatasource.getAll().map { it.toDomain() }
            if (local.isNotEmpty()) {
                Log.d("BikeRepository", "Loaded ${local.size} bikes from database")
                local.forEach { bike ->
                    Log.d("BikeRepository", "Database bike: UUID=${bike.uuid}, Name='${bike.name}', Active=${bike.isActive}")
                }
                return@withContext local.filter { !it.inMaintenance }
            }

            if (isNetworkAvailable() && !getAuthToken().isNullOrEmpty()) {
                Log.d("BikeRepository", "No local bikes found, fetching from API")
                val token = getAuthToken() ?: return@withContext emptyList()
                val remote = remoteDataSource.getBikes(token).map { it.toDomain() }
                Log.d("BikeRepository", "API returned ${remote.size} bikes")
                if (remote.isNotEmpty()) {
                    remote.forEach { bike ->
                        bikeDatasource.save(BikeDTO.fromDomain(bike))
                        Log.d("BikeRepository", "Saved API bike to database: UUID=${bike.uuid}, Name='${bike.name}', Lat=${bike.lat}, Lon=${bike.lon}")
                    }
                    return@withContext remote.filter { !it.inMaintenance }
                } else {
                    Log.d("BikeRepository", "API returned an empty list of bikes")
                    return@withContext emptyList()
                }
            } else {
                Log.d("BikeRepository", "No network or token to fetch bikes")
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            Log.e("BikeRepository", "Error in getAll(): ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Obtiene todas las bicicletas filtradas por una categoría dada, excluyendo las que están en mantenimiento.
     *
     * @param category Nombre de la categoría para filtrar las bicicletas.
     * @return Lista de bicicletas que coinciden con la categoría.
     */
    override suspend fun getBikesByCategory(category: String): List<Bike> {
        val bikes = bikeDatasource.getAll()
            .filter { it.bikeTypeName.equals(category, ignoreCase = true) }
            .map { it.toDomain() }
            .filter { !it.inMaintenance }
        Log.d("BikeRepository", "Found ${bikes.size} bikes in category $category")
        return bikes
    }

    /**
     * Actualiza el estado de actividad de una bicicleta por su identificador.
     *
     * @param bikeId UUID de la bicicleta.
     * @param isActive Nuevo estado de actividad.
     */
    suspend fun updateBikeStatus(bikeId: String, isActive: Boolean) {
        bikeDatasource.updateBikeStatus(bikeId, isActive)
        Log.d("BikeRepository", "Updated bike status: ID=$bikeId, isActive=$isActive")
    }

    /**
     * Actualiza el estado de alquiler de una bicicleta por su identificador.
     *
     * @param bikeId UUID de la bicicleta.
     * @param isRented Nuevo estado de alquiler.
     */
    suspend fun updateBikeRentStatus(bikeId: String, isRented: Boolean) {
        bikeDatasource.updateBikeRentStatus(bikeId, isRented)
        Log.d("BikeRepository", "Updated bike rent status: ID=$bikeId, isRented=$isRented")
    }

    /**
     * Obtiene todas las bicicletas marcadas como activas de la fuente de datos local.
     *
     * @return Lista de objetos BikeDTO activos.
     */
    override suspend fun getActiveBikes(): List<BikeDTO> {
        val activeBikes = bikeDatasource.getAll().filter { it.isActive }
        Log.d("BikeRepository", "Found ${activeBikes.size} active bikes in database")
        return activeBikes
    }

    /**
     * Actualiza el estado de alquiler de una bicicleta dada.
     *
     * @param bike Modelo de dominio Bike con el estado de alquiler actualizado.
     * @return true si la actualización fue exitosa; false en caso contrario.
     */
    override suspend fun updateBikeRentStatus(bike: Bike): Boolean = try {
        bikeDatasource.save(BikeDTO.fromDomain(bike))
        Log.d("BikeRepository", "Successfully updated rent status for bike: UUID=${bike.uuid}, isRented=${bike.isRented}")
        true
    } catch (e: Exception) {
        Log.e("BikeRepository", "Update rent status failed for bike ${bike.uuid}: ${e.message}", e)
        false
    }

    /**
     * Obtiene el estado actual del servidor desde la API remota.
     *
     * @return Objeto ServerStatus que representa la información del servidor.
     */
    override suspend fun status(): ServerStatus {
        val status = remoteDataSource.getStatus()
        Log.d("BikeRepository", "Fetched server status: Version=${status.version}, Name=${status.name}")
        return status
    }

    /**
     * Obtiene la primera bicicleta activa de la lista completa de bicicletas.
     *
     * @return Primera bicicleta activa si existe; null en caso contrario.
     */
    suspend fun getFirstActiveBike(): Bike? {
        val firstActive = getAll().firstOrNull { it.isActive }
        if (firstActive != null) {
            Log.d("BikeRepository", "Found first active bike: UUID=${firstActive.uuid}, Name='${firstActive.name}'")
        } else {
            Log.d("BikeRepository", "No active bikes found")
        }
        return firstActive
    }
}