package com.example.examenhockey

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/**
 * Activity principal del juego - Muestra el menú con las opciones principales
 */
class MenuPrincipalActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)
        
        // Ocultar ActionBar para pantalla completa
        supportActionBar?.hide()
        
        configurarBotones()
    }
    
    /**
     * Configura los listeners de los botones del menú
     */
    private fun configurarBotones() {
        findViewById<MaterialButton>(R.id.btnUnJugador).setOnClickListener {
            iniciarJuego(modoUnJugador = true)
        }
        
        findViewById<MaterialButton>(R.id.btnMultijugador).setOnClickListener {
            iniciarJuego(modoUnJugador = false)
        }
        
        findViewById<MaterialButton>(R.id.btnSalir).setOnClickListener {
            finish()
        }
    }
    
    /**
     * Inicia el juego con el modo seleccionado
     */
    private fun iniciarJuego(modoUnJugador: Boolean) {
        val intent = Intent(this, JuegoActivity::class.java)
        intent.putExtra("MODO_UN_JUGADOR", modoUnJugador)
        startActivity(intent)
    }
}

