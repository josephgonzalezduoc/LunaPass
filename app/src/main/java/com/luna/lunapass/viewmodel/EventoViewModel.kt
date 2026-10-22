package com.luna.lunapass.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.luna.lunapass.model.Evento
import com.luna.lunapass.repository.EventoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.UUID

data class EventoFormulario(
    val nombre: String = "",
    val lugar: String = "",
    val capacidad: String = "",
    val portadaPath: String = "",
    val cargandoPortada: Boolean = false,
    val errorPortada: String? = null,
    val errorNombre: String? = null,
    val errorLugar: String? = null,
    val errorCapacidad: String? = null
)

class EventoViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository = EventoRepository(application)

    // Permite ignorar una carga si se abrió un formulario nuevo.
    private var versionFormulario = 0

    var eventos by mutableStateOf(repository.obtenerEventos())
        private set

    var formulario by mutableStateOf(EventoFormulario())
        private set

    fun cambiarNombre(valor: String) {
        formulario = formulario.copy(
            nombre = valor,
            errorNombre = null
        )
    }

    fun cambiarLugar(valor: String) {
        formulario = formulario.copy(
            lugar = valor,
            errorLugar = null
        )
    }

    fun cambiarCapacidad(valor: String) {
        formulario = formulario.copy(
            capacidad = valor,
            errorCapacidad = null
        )
    }

    fun seleccionarPortada(uri: Uri) {
        if (formulario.cargandoPortada) return

        val versionActual = versionFormulario

        formulario = formulario.copy(
            cargandoPortada = true,
            errorPortada = null
        )

        viewModelScope.launch {
            try {
                val ruta = repository.copiarPortada(uri)

                if (versionActual == versionFormulario) {
                    formulario = formulario.copy(
                        portadaPath = ruta,
                        cargandoPortada = false,
                        errorPortada = null
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (versionActual == versionFormulario) {
                    formulario = formulario.copy(
                        cargandoPortada = false,
                        errorPortada = "No se pudo cargar la imagen. " +
                                "Intenta con otra."
                    )
                }
            }
        }
    }

    fun validar(): Boolean {
        val nombre = formulario.nombre.trim()
        val lugar = formulario.lugar.trim()
        val capacidad = formulario.capacidad.toIntOrNull()

        val errorNombre = when {
            nombre.isBlank() -> "Ingresa el nombre del evento"
            nombre.length < 3 -> "Usa al menos 3 caracteres"
            else -> null
        }

        val errorLugar = when {
            lugar.isBlank() -> "Ingresa el lugar del evento"
            lugar.length < 3 -> "Usa al menos 3 caracteres"
            else -> null
        }

        val errorCapacidad = when {
            capacidad == null -> "Ingresa un número entero válido"
            capacidad <= 0 -> "La capacidad debe ser mayor que cero"
            else -> null
        }

        formulario = formulario.copy(
            errorNombre = errorNombre,
            errorLugar = errorLugar,
            errorCapacidad = errorCapacidad
        )

        return errorNombre == null &&
                errorLugar == null &&
                errorCapacidad == null &&
                !formulario.cargandoPortada
    }

    fun guardarEvento(): Boolean {
        if (!validar()) return false

        val nuevoEvento = Evento(
            id = UUID.randomUUID().toString(),
            nombre = formulario.nombre.trim(),
            lugar = formulario.lugar.trim(),
            capacidad = formulario.capacidad.toInt(),
            portadaPath = formulario.portadaPath
        )

        val listaActualizada = eventos + nuevoEvento

        repository.guardarEventos(listaActualizada)
        eventos = listaActualizada
        limpiar()

        return true
    }

    fun limpiar() {
        versionFormulario++
        formulario = EventoFormulario()
    }
}