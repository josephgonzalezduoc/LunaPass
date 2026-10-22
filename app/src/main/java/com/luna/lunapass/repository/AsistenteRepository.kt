package com.luna.lunapass.repository

import android.content.Context
import com.luna.lunapass.model.Asistente
import org.json.JSONArray
import org.json.JSONObject

class AsistenteRepository(context: Context) {

    private val preferencias = context.applicationContext
        .getSharedPreferences(
            "lunapass_asistentes",
            Context.MODE_PRIVATE
        )

    fun obtenerAsistentes(): List<Asistente> {
        val contenido = preferencias.getString("asistentes", "[]") ?: "[]"
        val arreglo = JSONArray(contenido)

        return List(arreglo.length()) { indice ->
            val objeto = arreglo.getJSONObject(indice)

            Asistente(
                id = objeto.getString("id"),
                eventoId = objeto.getString("eventoId"),
                nombre = objeto.getString("nombre"),
                correo = objeto.getString("correo"),
                ingresoRegistrado = objeto.optBoolean(
                    "ingresoRegistrado",
                    false
                )
            )
        }
    }

    fun guardarAsistentes(asistentes: List<Asistente>) {
        val arreglo = JSONArray()

        asistentes.forEach { asistente ->
            arreglo.put(
                JSONObject().apply {
                    put("id", asistente.id)
                    put("eventoId", asistente.eventoId)
                    put("nombre", asistente.nombre)
                    put("correo", asistente.correo)
                    put("ingresoRegistrado", asistente.ingresoRegistrado)
                }
            )
        }

        preferencias.edit()
            .putString("asistentes", arreglo.toString())
            .apply()
    }
}