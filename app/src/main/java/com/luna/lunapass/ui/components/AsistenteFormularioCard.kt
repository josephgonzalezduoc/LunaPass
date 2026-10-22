package com.luna.lunapass.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.luna.lunapass.viewmodel.AsistenteFormulario

@Composable
fun AsistenteFormularioCard(
    formulario: AsistenteFormulario,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onRegistrar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Registrar asistente",
                style = MaterialTheme.typography.titleLarge
            )

            CampoValidado(
                valor = formulario.nombre,
                onCambio = onNombreChange,
                etiqueta = "Nombre del asistente",
                error = formulario.errorNombre
            )

            CampoValidado(
                valor = formulario.correo,
                onCambio = onCorreoChange,
                etiqueta = "Correo electrónico",
                error = formulario.errorCorreo,
                teclado = KeyboardType.Email
            )

            AnimatedVisibility(
                visible = formulario.errorGeneral != null
            ) {
                Text(
                    text = formulario.errorGeneral.orEmpty(),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onRegistrar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar asistente")
            }
        }
    }
}