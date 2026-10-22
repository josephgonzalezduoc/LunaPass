package com.luna.lunapass.model

data class Evento(
    val id: String,
    val nombre: String,
    val lugar: String,
    val capacidad: Int,
    val portadaPath: String = ""
)