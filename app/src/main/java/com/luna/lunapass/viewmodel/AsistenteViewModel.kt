package com.luna.lunapass.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.luna.lunapass.model.Asistente
import com.luna.lunapass.model.Evento
import com.luna.lunapass.repository.AsistenteRepository
import java.util.Locale
import java.util.UUID

data class AsistenteFormulario(
    val nombre: String = "",
    val correo: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorGeneral: String? = null
)

class AsistenteViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository = AsistenteRepository(application)

    var asistentes by mutableStateOf(repository.obtenerAsistentes())
        private set

    var formulario by mutableStateOf(AsistenteFormulario())
        private set

    fun cambiarNombre(valor: String) {
        formulario = formulario.copy(
            nombre = valor,
            errorNombre = null,
            errorGeneral = null
        )
    }

    fun cambiarCorreo(valor: String) {
        formulario = formulario.copy(
            correo = valor,
            errorCorreo = null,
            errorGeneral = null
        )
    }

    fun limpiar() {
        formulario = AsistenteFormulario()
    }

    fun registrar(evento: Evento): Boolean {
        val nombre = formulario.nombre.trim()
        val correo = formulario.correo.trim()
            .lowercase(Locale.ROOT)

        val asistentesDelEvento = asistentes.filter {
            it.eventoId == evento.id
        }

        val errorNombre = when {
            nombre.isBlank() -> "Ingresa el nombre"
            nombre.length < 3 -> "Usa al menos 3 caracteres"
            else -> null
        }

        val errorCorreo = when {
            correo.isBlank() -> "Ingresa el correo"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                "Ingresa un correo válido"
            asistentesDelEvento.any {
                it.correo.equals(correo, ignoreCase = true)
            } -> "Este correo ya está registrado en el evento"
            else -> null
        }

        val errorGeneral = if (
            asistentesDelEvento.size >= evento.capacidad
        ) {
            "El evento ya alcanzó su capacidad máxima"
        } else {
            null
        }

        formulario = formulario.copy(
            errorNombre = errorNombre,
            errorCorreo = errorCorreo,
            errorGeneral = errorGeneral
        )

        if (
            errorNombre != null ||
            errorCorreo != null ||
            errorGeneral != null
        ) {
            return false
        }

        val nuevoAsistente = Asistente(
            id = UUID.randomUUID().toString(),
            eventoId = evento.id,
            nombre = nombre,
            correo = correo
        )

        val listaActualizada = asistentes + nuevoAsistente

        repository.guardarAsistentes(listaActualizada)
        asistentes = listaActualizada
        limpiar()

        return true
    }
    fun registrarIngreso(contenidoQr: String): String {
        val contenido = contenidoQr.trim()

        if (!contenido.startsWith("LUNAPASS:")) {
            return "QR inválido. No corresponde a una entrada LunaPass."
        }

        val id = contenido.removePrefix("LUNAPASS:")

        val asistente = asistentes.find {
            it.id == id
        } ?: return "Entrada no encontrada en este dispositivo."

        if (asistente.ingresoRegistrado) {
            return "ENTRADA YA UTILIZADA\n\n" +
                    "${asistente.nombre} ya tiene su ingreso registrado."
        }

        val listaActualizada = asistentes.map {
            if (it.id == asistente.id) {
                it.copy(ingresoRegistrado = true)
            } else {
                it
            }
        }

        repository.guardarAsistentes(listaActualizada)
        asistentes = listaActualizada

        return "ACCESO AUTORIZADO\n\n" +
                "Bienvenido/a, ${asistente.nombre}."
    }
}