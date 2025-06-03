package cat.deim.asm_22.p2_patinfly.data.datasource.remote

import android.util.Log
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.model.BikeApiModel
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.model.VehiclesResponse
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.network.RetrofitBike
import cat.deim.asm_22.p2_patinfly.domain.models.Bike
import cat.deim.asm_22.p2_patinfly.domain.models.ServerStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

/**
 * Fuente de datos remota para acceder a información relacionada con bicicletas y estado del servidor.
 * Implementa un singleton para asegurar una única instancia en toda la aplicación.
 */
class BikeRemoteDataSource private constructor() {
    companion object {
        private const val TAG = "BikeRemoteDataSource"

        @Volatile
        private var instance: BikeRemoteDataSource? = null

        /**
         * Obtiene la instancia singleton de [BikeRemoteDataSource].
         *
         * @return instancia única de [BikeRemoteDataSource]
         */
        fun getInstance(): BikeRemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: BikeRemoteDataSource().also {
                    instance = it
                }
            }
    }

    private val apiService: APIService
        get() = RetrofitBike.apiService

    /**
     * Obtiene el estado del servidor desde la API remota.
     *
     * Realiza la llamada en un contexto IO, maneja errores HTTP y generales.
     * En caso de error, devuelve un estado con valores por defecto indicativos.
     *
     * @return [ServerStatus] con la información actual del servidor o un estado de error.
     */
    suspend fun getStatus(): ServerStatus = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching server status from API")
        try {
            val status = apiService.getServerStatus()
            Log.d(TAG, "Server status received: Version=${status.version}, Name=${status.name}")
            status.toDomain()
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error fetching server status: Code=${e.code()}, Message=${e.message()}", e)
            ServerStatus(
                version = "0.0",
                build = "0",
                update = "",
                name = "error"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching server status: ${e.message}", e)
            ServerStatus(
                version = "0.0",
                build = "0",
                update = "",
                name = "error"
            )
        }
    }

    /**
     * Obtiene la lista de bicicletas asociadas al usuario autenticado.
     *
     * @param token Token de autenticación en formato Bearer.
     * @return Lista de [BikeApiModel] si la petición fue exitosa; lista vacía en caso contrario.
     */
    suspend fun getBikes(token: String): List<BikeApiModel> {
        Log.d(TAG, "Initiating getBikes request with token: ${token.take(10)}...")
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<VehiclesResponse> = apiService.getBikes("Bearer $token")
                Log.d(TAG, "API response for getBikes: Code=${response.code()}, Message=${response.message()}, Headers=${response.headers()}")
                if (response.isSuccessful) {
                    val bikes = response.body()?.vehicles ?: emptyList()
                    Log.d(TAG, "API returned ${bikes.size} bikes from /api/vehicle endpoint")
                    bikes.forEach { bike ->
                        Log.d(TAG, "Bike fetched: ID=${bike.id}, Name='${bike.name}', Active=${bike.isActive}, Rented=${bike.isRented}")
                    }
                    bikes
                } else {
                    Log.e(TAG, "Failed to fetch bikes: Code=${response.code()}, Message=${response.message()}, ErrorBody=${response.errorBody()?.string()}")
                    emptyList()
                }
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error fetching bikes: Code=${e.code()}, Message=${e.message()}", e)
                emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Network/other error fetching bikes: ${e.message}", e)
                emptyList()
            }
        }
    }

    /**
     * Obtiene los detalles de una bicicleta específica dado su ID.
     *
     * @param id Identificador único de la bicicleta.
     * @param token Token de autenticación en formato Bearer.
     * @return Objeto [BikeApiModel] si se encontró y la llamada fue exitosa, o null en caso contrario.
     */
    suspend fun getBikeById(id: String, token: String): BikeApiModel? {
        Log.d(TAG, "Initiating getBikeById request for ID=$id with token: ${token.take(10)}...")
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<BikeApiModel> = apiService.getBikeById("Bearer $token", id)
                Log.d(TAG, "API response for getBikeById($id): Code=${response.code()}, Message=${response.message()}, Headers=${response.headers()}")
                if (response.isSuccessful) {
                    val bike = response.body()
                    if (bike != null) {
                        Log.d(TAG, "Successfully fetched bike: ID=$id, Name='${bike.name}', Active=${bike.isActive}, Rented=${bike.isRented}")
                    } else {
                        Log.d(TAG, "No bike found in API response for ID=$id")
                    }
                    bike
                } else {
                    Log.e(TAG, "Failed to fetch bike with ID=$id: Code=${response.code()}, Message=${response.message()}, ErrorBody=${response.errorBody()?.string()}")
                    null
                }
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error fetching bike with ID=$id: Code=${e.code()}, Message=${e.message()}", e)
                null
            } catch (e: Exception) {
                Log.e(TAG, "Network/other error fetching bike with ID=$id: ${e.message}", e)
                null
            }
        }
    }

    /**
     * Obtiene todas las bicicletas en formato de dominio ([Bike]).
     *
     * @param token Token de autenticación en formato Bearer.
     * @return Colección de objetos [Bike]; vacía si la petición falla o no hay datos.
     */
    suspend fun getAll(token: String): Collection<Bike> {
        Log.d(TAG, "Initiating getAll with token: ${token.take(10)}...")
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getBikes("Bearer $token")
                if (response.isSuccessful) {
                    val bikes = response.body()?.vehicles ?: emptyList()
                    Log.d(TAG, "Bicicletas obtenidas: ${bikes.size}")
                    bikes.map { it.toDomain() }
                } else {
                    Log.e(TAG, "Error fetching bikes: ${response.code()} ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error general en getAll(): ${e.message}", e)
                emptyList()
            }
        }
    }
}
