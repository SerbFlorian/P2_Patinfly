package cat.deim.asm_22.p2_patinfly.domain.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import cat.deim.asm_22.p2_patinfly.data.repository.BikeRepository

/**
 * Representa el estado del servidor con información básica como versión, build, actualización y nombre.
 *
 * @property version Versión del servidor.
 * @property build Número de build o compilación.
 * @property update Fecha o descripción de la última actualización.
 * @property name Nombre del servidor.
 */
data class ServerStatus(
    val version: String,
    val build: String,
    val update: String,
    val name: String
) {
    companion object {
        /**
         * Obtiene de manera asíncrona el estado del servidor.
         *
         * Esta función devuelve un [LiveData] que emite un [Result] con el
         * estado del servidor en caso de éxito, o un error en caso de fallo.
         *
         * @param context Contexto de la aplicación necesario para crear el repositorio.
         * @return [LiveData] que emite un [Result] con el [ServerStatus] o una excepción.
         */
        fun execute(context: Context): LiveData<Result<ServerStatus>> = liveData {
            try {
                // Obtener una instancia del repositorio usando su función create()
                val repository = BikeRepository.create(context)

                // Obtener el estado del servidor desde el repositorio
                val status = repository.status()

                // Emitir el resultado exitoso
                emit(Result.success(status))
            } catch (e: Exception) {
                // Emitir un resultado de fallo con la excepción capturada
                emit(Result.failure(e))
            }
        }
    }
}
