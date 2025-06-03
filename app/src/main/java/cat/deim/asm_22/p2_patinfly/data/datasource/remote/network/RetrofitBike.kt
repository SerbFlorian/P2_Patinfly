package cat.deim.asm_22.p2_patinfly.data.datasource.remote.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import android.util.Log
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.APIService
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Singleton encargado de configurar e inicializar Retrofit para acceder a la API de Patinfly.
 *
 * Configura un cliente HTTP con tiempos de espera y un interceptor para añadir cabeceras de autorización,
 * usando un token dinámico si está disponible, o un token estático por defecto.
 * También configura Kotlinx Serialization para el manejo de JSON.
 */
object RetrofitBike {
    private const val BASE_URL = "https://api.patinfly.dev/"

    /**
     * Token estático usado cuando no se proporciona uno dinámico.
     */
    private const val STATIC_API_KEY = ""

    private const val TAG = "RetrofitBike"

    /**
     * Configuración del parser JSON con Kotlinx Serialization:
     * - ignoreUnknownKeys: Ignora campos que no están modelados en las clases,
     *   permitiendo compatibilidad con cambios en la API.
     * - isLenient: Permite parsear JSONs que no son estrictamente correctos,
     *   mejorando tolerancia a formatos inesperados.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Servicio Retrofit generado para realizar peticiones a la API.
     * Se inicializa llamando a [initialize].
     */
    lateinit var apiService: APIService

    /**
     * Inicializa Retrofit con un cliente OkHttp que añade un interceptor para:
     * - Agregar cabecera "Authorization" con token Bearer dinámico si está disponible,
     *   o con un token estático por defecto en caso contrario.
     * - Añadir cabecera "Origin" vacía para evitar problemas de CORS.
     *
     * Además, configura tiempos de espera para conexión, lectura y escritura a 30 segundos.
     *
     * @param tokenProvider Función que debe devolver el token de autorización dinámico,
     * o null si no hay token disponible.
     *
     * Ejemplo de uso:
     * RetrofitBike.initialize { userSession.getToken() }
     */
    fun initialize(tokenProvider: () -> String?) {
        Log.d(TAG, "Inicializando RetrofitBike")

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para establecer conexión
            .readTimeout(30, TimeUnit.SECONDS)    // Tiempo máximo para leer respuesta
            .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo máximo para enviar petición
            .addInterceptor(Interceptor { chain ->
                var request = chain.request()

                val dynamicAuthToken = tokenProvider()
                Log.d(TAG, "Requesting URL: ${request.url}")

                // Decide si se usa token dinámico o token estático
                val authHeaderValue = if (!dynamicAuthToken.isNullOrEmpty()) {
                    Log.d(TAG, "Using Dynamic Authorization: Bearer ${dynamicAuthToken.take(10)}...")
                    "Bearer $dynamicAuthToken"
                } else {
                    Log.d(TAG, "Using Static Authorization: Bearer ${STATIC_API_KEY.take(10)}...")
                    "Bearer $STATIC_API_KEY"
                }

                // Reconstruye la petición con las cabeceras de autorización y origen
                request = request.newBuilder()
                    .header("Authorization", authHeaderValue)
                    .header("Origin", "") // Cabecera para evitar bloqueos CORS en algunas APIs
                    .build()

                chain.proceed(request)
            })
            .build()

        // Configura Retrofit con la URL base, cliente HTTP y el convertidor JSON
        apiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(APIService::class.java)
    }
}
