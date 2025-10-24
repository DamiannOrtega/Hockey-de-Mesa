package com.example.examenhockey

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Clase para gestionar el guardado y recuperación de puntuaciones usando SharedPreferences
 */
class GestorPuntuaciones(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(NOMBRE_PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val NOMBRE_PREFS = "puntuaciones_hockey"
        private const val CLAVE_PUNTUACIONES = "lista_puntuaciones"
        private const val MAX_PUNTUACIONES = 10 // Guardar top 10
    }
    
    /**
     * Guarda una nueva puntuación
     */
    fun guardarPuntuacion(puntuacion: Puntuacion) {
        val lista = obtenerPuntuaciones().toMutableList()
        lista.add(puntuacion)
        
        // Ordenar por puntos descendente y tomar las mejores 10
        val listaOrdenada = lista
            .sortedByDescending { it.puntos }
            .take(MAX_PUNTUACIONES)
        
        val json = gson.toJson(listaOrdenada)
        prefs.edit().putString(CLAVE_PUNTUACIONES, json).apply()
    }
    
    /**
     * Obtiene todas las puntuaciones guardadas
     */
    fun obtenerPuntuaciones(): List<Puntuacion> {
        val json = prefs.getString(CLAVE_PUNTUACIONES, null) ?: return emptyList()
        
        val tipo = object : TypeToken<List<Puntuacion>>() {}.type
        return try {
            gson.fromJson(json, tipo)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Limpia todas las puntuaciones
     */
    fun limpiarPuntuaciones() {
        prefs.edit().remove(CLAVE_PUNTUACIONES).apply()
    }
}

