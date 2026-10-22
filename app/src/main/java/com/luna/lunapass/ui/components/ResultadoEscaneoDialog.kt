package com.luna.lunapass.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun ResultadoEscaneoDialog(
    mensaje: String,
    onCerrar: () -> Unit
) {
    // Estos textos coinciden con los que devuelve el ViewModel actual.
    val autorizado = mensaje.startsWith("ACCESO AUTORIZADO")
    val utilizada = mensaje.startsWith("ENTRADA YA UTILIZADA")

    val titulo = when {
        autorizado -> "Acceso autorizado"
        utilizada -> "Entrada ya utilizada"
        else -> "Entrada no válida"
    }

    val color = when {
        autorizado -> Color(0xFF2E7D32)
        utilizada -> Color(0xFF9C5700)
        else -> MaterialTheme.colorScheme.error
    }

    val detalle = if (autorizado || utilizada) {
        mensaje.substringAfter("\n\n", mensaje)
    } else {
        mensaje
    }

    val progreso = remember(mensaje) {
        Animatable(0f)
    }

    LaunchedEffect(mensaje) {
        progreso.animateTo(
            targetValue = 1f,
            animationSpec = tween(350)
        )
    }

    AlertDialog(
        onDismissRequest = onCerrar,

        icon = {
            Icon(
                imageVector = if (autorizado) {
                    Icons.Filled.CheckCircle
                } else {
                    Icons.Filled.Warning
                },
                contentDescription = titulo,
                tint = color,
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        alpha = progreso.value
                        scaleX = 0.7f + 0.3f * progreso.value
                        scaleY = 0.7f + 0.3f * progreso.value
                    }
            )
        },

        title = {
            Text(
                text = titulo,
                color = color
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(detalle)

                if (autorizado) {
                    Text(
                        text = "El ingreso quedó guardado.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },

        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text("Aceptar")
            }
        }
    )
}