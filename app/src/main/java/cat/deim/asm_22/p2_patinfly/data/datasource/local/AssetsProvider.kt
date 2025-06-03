package cat.deim.asm_22.p2_patinfly.data.datasource.local

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log

/**
 * Objeto utilitario para proporcionar acceso a archivos JSON almacenados en la carpeta `res/raw/`.
 */
object AssetsProvider {
    /**
     * Carga un archivo JSON desde la carpeta `res/raw/` y lo devuelve como un [String].
     *
     * Busca el recurso por nombre (sin extensión) dentro del paquete de la aplicación,
     * abre el archivo raw asociado, y devuelve su contenido completo como texto.
     *
     * En caso de que no se encuentre el archivo o ocurra un error durante la lectura,
     * se devuelve `null` y se registra un error en el log.
     *
     * @param context Contexto de la aplicación, necesario para acceder a los recursos.
     * @param fileName Nombre del archivo JSON sin la extensión (por ejemplo, para `data.json` usar `"data"`).
     * @return Contenido del archivo JSON como [String], o `null` si no se pudo cargar.
     */
    @SuppressLint("DiscouragedApi")
    fun getJsonDataFromRawAsset(context: Context, fileName: String): String? {
        return try {
            val resourceId = context.resources.getIdentifier(fileName, "raw", context.packageName)

            if (resourceId == 0) {
                Log.e("AssetsProvider", "Archivo no encontrado: $fileName en res/raw/")
                return null
            }

            Log.d("AssetsProvider", "Cargando archivo JSON: $fileName con ID: $resourceId")

            context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e("AssetsProvider", "Error al leer el archivo JSON: ${e.message}")
            null
        }
    }
}
