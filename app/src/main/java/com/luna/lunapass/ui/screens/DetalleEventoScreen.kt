package com.luna.lunapass.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luna.lunapass.model.Asistente
import com.luna.lunapass.model.Evento
import com.luna.lunapass.ui.components.AsistenteFormularioCard
import com.luna.lunapass.viewmodel.AsistenteFormulario
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.luna.lunapass.ui.components.EntradaQrDialog

@Composable
fun DetalleEventoScreen(
    evento: Evento,
    asistentes: List<Asistente>,
    formulario: AsistenteFormulario,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onRegistrar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var entradaSeleccionadaId by rememberSaveable(evento.id) {
        mutableStateOf<String?>(null)
    }

    val asistenteSeleccionado = asistentes.find {
        it.id == entradaSeleccionadaId &&
                it.eventoId == evento.id
    }

    if (asistenteSeleccionado != null) {
        EntradaQrDialog(
            asistente = asistenteSeleccionado,
            nombreEvento = evento.nombre,
            onCerrar = {
                entradaSeleccionadaId = null
            }
        )
    }

    val asistentesDelEvento = asistentes.filter {
        it.eventoId == evento.id
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TextButton(onClick = onVolver) {
            Text("Volver")
        }

        Text(
            text = evento.nombre,
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Información del evento",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Lugar: ${evento.lugar}")

                Text(
                    text = "Inscritos: ${asistentesDelEvento.size}" +
                            " / ${evento.capacidad}"
                )
                Text(
                    text = "Ingresos registrados: " +
                            asistentesDelEvento.count {
                                it.ingresoRegistrado
                            }
                )

                Text(
                    text = "Cupos disponibles: " +
                            (evento.capacidad - asistentesDelEvento.size)
                )
            }
        }

        AsistenteFormularioCard(
            formulario = formulario,
            onNombreChange = onNombreChange,
            onCorreoChange = onCorreoChange,
            onRegistrar = onRegistrar
        )

        Text(
            text = "Asistentes registrados",
            style = MaterialTheme.typography.titleLarge
        )

        if (asistentesDelEvento.isEmpty()) {
            Text("Todavía no hay asistentes registrados.")
        } else {
            asistentesDelEvento.forEach { asistente ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = asistente.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(asistente.correo)
                        TextButton(
                            onClick = {
                                entradaSeleccionadaId = asistente.id
                            }
                        ) {
                            Text("Ver entrada QR")
                        }

                        Text(
                            text = if (asistente.ingresoRegistrado) {
                                "Ingreso registrado"
                            } else {
                                "Pendiente de ingreso"
                            }
                        )
                    }
                }
            }
        }
    }
}