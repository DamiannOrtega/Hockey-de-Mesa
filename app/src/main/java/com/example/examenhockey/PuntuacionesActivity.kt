package com.example.examenhockey

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/**
 * Activity que muestra las mejores puntuaciones guardadas
 */
class PuntuacionesActivity : AppCompatActivity() {

    private lateinit var contenedorPuntuaciones: LinearLayout
    private lateinit var gestorPuntuaciones: GestorPuntuaciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puntuaciones)

        // Ocultar ActionBar
        supportActionBar?.hide()

        gestorPuntuaciones = GestorPuntuaciones(this)

        inicializarVistas()
        configurarBotones()
        cargarPuntuaciones()
    }

    /**
     * Inicializa las vistas
     */
    private fun inicializarVistas() {
        contenedorPuntuaciones = findViewById(R.id.contenedorPuntuaciones)
    }

    /**
     * Configura los botones
     */
    private fun configurarBotones() {
        findViewById<MaterialButton>(R.id.btnVolver).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnLimpiar).setOnClickListener {
            mostrarDialogoLimpiar()
        }
    }

    /**
     * Carga y muestra las puntuaciones
     */
    private fun cargarPuntuaciones() {
        contenedorPuntuaciones.removeAllViews()

        val puntuaciones = gestorPuntuaciones.obtenerPuntuaciones()

        if (puntuaciones.isEmpty()) {
            mostrarMensajeSinPuntuaciones()
        } else {
            puntuaciones.forEachIndexed { index, puntuacion ->
                agregarItemPuntuacion(index + 1, puntuacion)
            }
        }
    }

    /**
     * Muestra un mensaje cuando no hay puntuaciones
     */
    private fun mostrarMensajeSinPuntuaciones() {
        val textView = TextView(this).apply {
            text = getString(R.string.sin_puntuaciones)
            textSize = 18f
            setTextColor(getColor(R.color.textoGris))
            gravity = android.view.Gravity.CENTER
            setPadding(32, 64, 32, 64)
        }
        contenedorPuntuaciones.addView(textView)
    }

    /**
     * Agrega un item de puntuación a la lista
     */
    private fun agregarItemPuntuacion(posicion: Int, puntuacion: Puntuacion) {
        val inflater = LayoutInflater.from(this)
        val vista = inflater.inflate(R.layout.item_puntuacion, contenedorPuntuaciones, false)

        vista.findViewById<TextView>(R.id.txtPosicion).text = posicion.toString()
        vista.findViewById<TextView>(R.id.txtNombre).text = puntuacion.nombre
        vista.findViewById<TextView>(R.id.txtPuntos).text = "${puntuacion.puntos} pts"

        val detalle = "${puntuacion.modo} • ${puntuacion.obtenerFechaFormateada()}"
        vista.findViewById<TextView>(R.id.txtDetalle).text = detalle

        // Colorear los primeros 3 puestos de manera especial
        val colorPosicion = when (posicion) {
            1 -> android.graphics.Color.parseColor("#FFD700") // Oro
            2 -> android.graphics.Color.parseColor("#C0C0C0") // Plata
            3 -> android.graphics.Color.parseColor("#CD7F32") // Bronce
            else -> getColor(R.color.azulClaro)
        }

        vista.findViewById<TextView>(R.id.txtPosicion).apply {
            setTextColor(android.graphics.Color.WHITE)
            background.setTint(colorPosicion)
        }

        contenedorPuntuaciones.addView(vista)

        // Agregar separador excepto en el último item
        if (posicion < gestorPuntuaciones.obtenerPuntuaciones().size) {
            agregarSeparador()
        }
    }

    /**
     * Agrega un separador visual entre items
     */
    private fun agregarSeparador() {
        val separador = android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
            ).apply {
                setMargins(40, 8, 40, 8)
            }
            setBackgroundColor(getColor(R.color.textoGris))
            alpha = 0.3f
        }
        contenedorPuntuaciones.addView(separador)
    }

    /**
     * Muestra el diálogo de confirmación para limpiar puntuaciones
     */
    private fun mostrarDialogoLimpiar() {
        AlertDialog.Builder(this)
            .setTitle("Limpiar Puntuaciones")
            .setMessage("¿Estás seguro de que deseas eliminar todas las puntuaciones? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                gestorPuntuaciones.limpiarPuntuaciones()
                cargarPuntuaciones()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

