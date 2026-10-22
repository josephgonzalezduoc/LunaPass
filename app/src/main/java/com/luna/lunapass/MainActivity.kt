package com.luna.lunapass

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.luna.lunapass.ui.components.ResultadoEscaneoDialog
import com.luna.lunapass.ui.screens.CrearEventoScreen
import com.luna.lunapass.ui.screens.DetalleEventoScreen
import com.luna.lunapass.ui.screens.HomeScreen
import com.luna.lunapass.ui.theme.LunaPassTheme
import com.luna.lunapass.viewmodel.AsistenteViewModel
import com.luna.lunapass.viewmodel.EventoViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory =
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)

        val eventoViewModel = ViewModelProvider(
            this,
            factory
        ).get(EventoViewModel::class.java)

        val asistenteViewModel = ViewModelProvider(
            this,
            factory
        ).get(AsistenteViewModel::class.java)

        enableEdgeToEdge()

        setContent {
            var resultadoEscaneo by rememberSaveable {
                mutableStateOf<String?>(null)
            }

            var mostrarFormulario by rememberSaveable {
                mutableStateOf(false)
            }

            var eventoSeleccionadoId by rememberSaveable {
                mutableStateOf<String?>(null)
            }

            val lectorQr = rememberLauncherForActivityResult(
                contract = ScanContract()
            ) { resultado ->
                val contenido = resultado.contents

                if (contenido != null) {
                    resultadoEscaneo =
                        asistenteViewModel.registrarIngreso(contenido)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Escaneo cancelado o cámara no disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val eventoSeleccionado = eventoViewModel.eventos.find {
                it.id == eventoSeleccionadoId
            }

            BackHandler(
                enabled = resultadoEscaneo == null &&
                        (mostrarFormulario || eventoSeleccionadoId != null)
            ) {
                mostrarFormulario = false
                eventoSeleccionadoId = null
            }

            LunaPassTheme {
                resultadoEscaneo?.let { mensaje ->
                    ResultadoEscaneoDialog(
                        mensaje = mensaje,
                        onCerrar = {
                            resultadoEscaneo = null
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    Crossfade(
                        targetState = Pair(
                            mostrarFormulario,
                            eventoSeleccionado
                        ),
                        animationSpec = tween(250),
                        label = "Transicion de pantallas",
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) { estado ->

                        val formularioVisible = estado.first
                        val eventoDetalle = estado.second

                        when {
                            formularioVisible -> {
                                CrearEventoScreen(
                                    formulario = eventoViewModel.formulario,

                                    onNombreChange =
                                        eventoViewModel::cambiarNombre,

                                    onLugarChange =
                                        eventoViewModel::cambiarLugar,

                                    onCapacidadChange =
                                        eventoViewModel::cambiarCapacidad,

                                    onPortadaSeleccionada =
                                        eventoViewModel::seleccionarPortada,

                                    onGuardar = {
                                        if (eventoViewModel.guardarEvento()) {
                                            mostrarFormulario = false

                                            Toast.makeText(
                                                this@MainActivity,
                                                "Evento guardado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },

                                    onVolver = {
                                        mostrarFormulario = false
                                    }
                                )
                            }

                            eventoDetalle != null -> {
                                DetalleEventoScreen(
                                    evento = eventoDetalle,

                                    asistentes =
                                        asistenteViewModel.asistentes,

                                    formulario =
                                        asistenteViewModel.formulario,

                                    onNombreChange =
                                        asistenteViewModel::cambiarNombre,

                                    onCorreoChange =
                                        asistenteViewModel::cambiarCorreo,

                                    onRegistrar = {
                                        if (
                                            asistenteViewModel.registrar(
                                                eventoDetalle
                                            )
                                        ) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Asistente registrado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },

                                    onVolver = {
                                        eventoSeleccionadoId = null
                                    }
                                )
                            }

                            else -> {
                                HomeScreen(
                                    eventos = eventoViewModel.eventos,

                                    onCrearEvento = {
                                        eventoViewModel.limpiar()
                                        mostrarFormulario = true
                                    },

                                    onEscanearEntrada = {
                                        val opciones = ScanOptions().apply {
                                            setDesiredBarcodeFormats(
                                                ScanOptions.QR_CODE
                                            )

                                            setPrompt(
                                                "Coloca la entrada QR " +
                                                        "dentro del recuadro"
                                            )

                                            setBeepEnabled(false)
                                            setBarcodeImageEnabled(false)
                                            setOrientationLocked(false)
                                        }

                                        lectorQr.launch(opciones)
                                    },

                                    onVerEvento = { id ->
                                        asistenteViewModel.limpiar()
                                        eventoSeleccionadoId = id
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}