package com.example.examenhockey

import java.text.SimpleDateFormat
import java.util.*

/**
 * Clase de datos para representar una puntuación guardada
 */
data class Puntuacion(
    val nombre: String,
    val puntos: Int,
    val fecha: Long = System.currentTimeMillis(),
    val modo: String // "Un Jugador" o "Multijugador"
) {
    /**
     * Formatea la fecha en un string legible
     */
    fun obtenerFechaFormateada(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formato.format(Date(fecha))
    }
}

