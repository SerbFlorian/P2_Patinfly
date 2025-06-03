package cat.deim.asm_22.p2_patinfly.data.datasource.remote.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import android.util.Log
import cat.deim.asm_22.p2_patinfly.data.datasource.remote.APIService
import java.util.concurrent.TimeUnit

/**
 * Singleton para configurar e inicializar Retrofit con OkHttpClient para acceder a la API de Patinfly.
 *
 * Este objeto prepara Retrofit con:
 * - Un cliente HTTP que establece tiempos de espera para conexión, lectura y escritura.
 * - Un interceptor que añade automáticamente cabeceras de autorización,
 *   usando un token dinámico proporcionado o un token estático por defecto.
 * - Un convertidor JSON basado en Kotlinx Serialization configurado para ignorar claves desconocidas y ser leniente.
 */
object RetrofitClient {

    /**
     * URL base de la API Patinfly.
     */
    private const val BASE_URL = "https://api.patinfly.dev/"

    /**
     * Token estático usado cuando no se proporciona uno dinámico.
     */
    private const val STATIC_API_KEY = ""

    /**
     * Configuración de Kotlinx Serialization para el parseo JSON.
     * - ignoreUnknownKeys: Ignora campos JSON no mapeados para mantener compatibilidad.
     * - isLenient: Permite parsear JSON que no cumple estrictamente el estándar.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Servicio API generado por Retrofit para hacer peticiones a la API.
     * Será inicializado mediante la llamada a [initialize].
     */
    lateinit var apiService: APIService

    /**
     * Inicializa RetrofitClient creando un cliente OkHttp con un interceptor para manejar
     * la autorización, configurando Retrofit con la base URL y el convertidor JSON.
     *
     * @param tokenProvider Función que debe devolver el token Bearer dinámico para autorización,
     * o null si no se dispone de token dinámico.
     *
     * El interceptor añade la cabecera "Authorization" con:
     * - "Bearer <token dinámico>" si se proporciona un token válido.
     * - "Bearer <token estático>" en caso contrario.
     *
     * También añade la cabecera "Origin" vacía para evitar problemas con CORS en algunas APIs.
     *
     * Configura además los siguientes tiempos máximos en el cliente HTTP:
     * - Tiempo de conexión: 30 segundos
     * - Tiempo de lectura: 30 segundos
     * - Tiempo de escritura: 30 segundos
     *
     * Ejemplo de uso:
     * RetrofitClient.initialize { sessionManager.getAuthToken() }
     */
    fun initialize(tokenProvider: () -> String?) {
        Log.d("RetrofitClient", "Inicializando RetrofitClient")

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // Tiempo máximo para establecer conexión con el servidor
            .readTimeout(30, TimeUnit.SECONDS)     // Tiempo máximo para esperar respuesta del servidor
            .writeTimeout(30, TimeUnit.SECONDS)    // Tiempo máximo para enviar la petición al servidor
            .addInterceptor(Interceptor { chain ->
                var request = chain.request()
                val dynamicAuthToken = tokenProvider()

                Log.d("RetrofitClient", "Requesting URL: ${request.url}")

                // Decide qué token usar para la cabecera de autorización
                val authHeaderValue = if (!dynamicAuthToken.isNullOrEmpty()) {
                    Log.d("RetrofitClient", "Using Dynamic Authorization: Bearer ${dynamicAuthToken.take(10)}...")
                    "Bearer $dynamicAuthToken"
                } else {
                    Log.d("RetrofitClient", "Using Static Authorization: Bearer ${STATIC_API_KEY.take(10)}...")
                    "Bearer $STATIC_API_KEY"
                }

                // Construye la nueva petición añadiendo las cabeceras necesarias
                request = request.newBuilder()
                    .header("Authorization", authHeaderValue)
                    .header("Origin", "")  // Cabecera para evitar bloqueos CORS en algunas configuraciones
                    .build()

                // Continúa con la petición usando la nueva configuración
                chain.proceed(request)
            })
            .build()

        // Configura Retrofit con la URL base, el cliente configurado y el convertidor JSON
        apiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(APIService::class.java)
    }
}
