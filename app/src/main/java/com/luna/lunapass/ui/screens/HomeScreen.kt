package com.luna.lunapass.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luna.lunapass.model.Evento
import androidx.compose.material3.HorizontalDivider
import com.luna.lunapass.ui.components.PortadaEvento

@Composable
fun HomeScreen(
    eventos: List<Evento>,
    onCrearEvento: () -> Unit,
    onEscanearEntrada: () -> Unit,
    onVerEvento: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "LunaPass",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tus eventos, una entrada a la vez.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Organiza tu próximo evento",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Registra asistentes y controla sus ingresos " +
                            "mediante entradas QR.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = onCrearEvento,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Crear evento")
                }
            }
        }

        OutlinedButton(
            onClick = onEscanearEntrada,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text("Escanear entrada QR")
        }

        Text(
            text = "Mis eventos",
            style = MaterialTheme.typography.titleLarge
        )

        if (eventos.isEmpty()) {
            Text(
                text = "Todavía no tienes eventos. Crea el primero para comenzar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            eventos.forEach { evento ->
                Card(
                    onClick = { onVerEvento(evento.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (evento.portadaPath.isNotBlank()) {
                            PortadaEvento(
                                ruta = evento.portadaPath,
                                descripcion = "Portada de ${evento.nombre}"
                            )
                        }
                        Text(
                            text = evento.nombre,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = evento.lugar,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        HorizontalDivider()

                        Text(
                            text = "Capacidad: ${evento.capacidad} personas",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}