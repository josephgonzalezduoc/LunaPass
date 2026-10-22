package com.luna.lunapass.model

data class Asistente(
    val id: String,
    val eventoId: String,
    val nombre: String,
    val correo: String,
    val ingresoRegistrado: Boolean = false
)