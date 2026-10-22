package com.luna.lunapass.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.luna.lunapass.ui.components.CampoValidado
import com.luna.lunapass.ui.components.PortadaEvento
import com.luna.lunapass.viewmodel.EventoFormulario

@Composable
fun CrearEventoScreen(
    formulario: EventoFormulario,
    onNombreChange: (String) -> Unit,
    onLugarChange: (String) -> Unit,
    onCapacidadChange: (String) -> Unit,
    onPortadaSeleccionada: (Uri) -> Unit,
    onGuardar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onPortadaSeleccionada(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onVolver) {
            Text("Volver")
        }

        Text(
            text = "Crear evento",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Completa los datos de tu evento.")

        AnimatedVisibility(
            visible = formulario.portadaPath.isNotBlank()
        ) {
            PortadaEvento(
                ruta = formulario.portadaPath,
                descripcion = "Portada del nuevo evento"
            )
        }

        OutlinedButton(
            onClick = {
                selectorImagen.launch("image/*")
            },
            enabled = !formulario.cargandoPortada,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (formulario.portadaPath.isBlank()) {
                    "Seleccionar portada"
                } else {
                    "Cambiar portada"
                }
            )
        }

        AnimatedVisibility(
            visible = formulario.cargandoPortada
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Guardando imagen…")
            }
        }

        formulario.errorPortada?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Text(
            text = "La portada es opcional.",
            style = MaterialTheme.typography.bodySmall
        )

        CampoValidado(
            valor = formulario.nombre,
            onCambio = onNombreChange,
            etiqueta = "Nombre del evento",
            error = formulario.errorNombre
        )

        CampoValidado(
            valor = formulario.lugar,
            onCambio = onLugarChange,
            etiqueta = "Lugar",
            error = formulario.errorLugar
        )

        CampoValidado(
            valor = formulario.capacidad,
            onCambio = onCapacidadChange,
            etiqueta = "Capacidad máxima",
            error = formulario.errorCapacidad,
            teclado = KeyboardType.Number
        )

        Button(
            onClick = onGuardar,
            enabled = !formulario.cargandoPortada,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Text(
                if (formulario.cargandoPortada) {
                    "Espera a que termine la imagen"
                } else {
                    "Guardar evento"
                }
            )
        }
    }
}