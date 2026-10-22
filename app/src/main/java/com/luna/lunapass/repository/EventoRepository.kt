package com.luna.lunapass.repository

import android.content.Context
import android.net.Uri
import com.luna.lunapass.model.Evento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class EventoRepository(context: Context) {

    private val appContext = context.applicationContext

    private val preferencias = appContext.getSharedPreferences(
        "lunapass_eventos",
        Context.MODE_PRIVATE
    )

    fun obtenerEventos(): List<Evento> {
        val contenido = preferencias.getString("eventos", "[]") ?: "[]"
        val arreglo = JSONArray(contenido)

        return List(arreglo.length()) { indice ->
            val objeto = arreglo.getJSONObject(indice)

            Evento(
                id = objeto.getString("id"),
                nombre = objeto.getString("nombre"),
                lugar = objeto.getString("lugar"),
                capacidad = objeto.getInt("capacidad"),
                portadaPath = objeto.optString("portadaPath", "")
            )
        }
    }

    fun guardarEventos(eventos: List<Evento>) {
        val arreglo = JSONArray()

        eventos.forEach { evento ->
            arreglo.put(
                JSONObject().apply {
                    put("id", evento.id)
                    put("nombre", evento.nombre)
                    put("lugar", evento.lugar)
                    put("capacidad", evento.capacidad)
                    put("portadaPath", evento.portadaPath)
                }
            )
        }

        preferencias.edit()
            .putString("eventos", arreglo.toString())
            .apply()
    }

    suspend fun copiarPortada(uri: Uri): String =
        withContext(Dispatchers.IO) {
            val carpeta = File(appContext.filesDir, "portadas")

            check(carpeta.exists() || carpeta.mkdirs()) {
                "No se pudo crear la carpeta de portadas"
            }

            val archivo = File(
                carpeta,
                "${UUID.randomUUID()}.img"
            )

            try {
                val entrada = appContext.contentResolver
                    .openInputStream(uri)
                    ?: error("No se pudo abrir la imagen")

                entrada.use { origen ->
                    archivo.outputStream().use { destino ->
                        origen.copyTo(destino)
                    }
                }

                check(archivo.length() > 0) {
                    "La imagen está vacía"
                }

                archivo.absolutePath
            } catch (error: Exception) {
                archivo.delete()
                throw error
            }
        }
}