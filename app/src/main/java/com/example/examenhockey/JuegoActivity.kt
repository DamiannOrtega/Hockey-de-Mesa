package com.example.examenhockey

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/**
 * Activity que maneja el juego de Air Hockey completo
 */
class JuegoActivity : AppCompatActivity() {

    // Vistas
    private lateinit var vistaJuego: VistaJuego
    private lateinit var txtPuntosJ1: TextView
    private lateinit var txtPuntosJ2: TextView
    private lateinit var txtNombreJ1: TextView
    private lateinit var txtNombreJ2: TextView
    private lateinit var txtTiempoJ1: TextView
    private lateinit var txtTiempoJ2: TextView
    private lateinit var txtContadorInicio: TextView
    private lateinit var txtMensajeJ1: TextView
    private lateinit var txtMensajeJ2: TextView
    private lateinit var btnIniciar: MaterialButton
    private lateinit var btnPausarJ1: MaterialButton
    private lateinit var btnPausarJ2: MaterialButton

    // Variables del juego
    private var puntosJ1 = 0
    private var puntosJ2 = 0
    private var tiempoRestante = 120 //Minutos en segundos que dura el juego
    private var contadorInicio = 3 // Contador de 3, 2, 1 para iniciar
    private var modoUnJugador = false
    private var juegoActivo = false
    private var temporizadorIniciado = false
    private var contandoInicio = false
    private var ultimoTiempo = 0L

    // Temporizador
    private val handler = Handler(Looper.getMainLooper())
    
    // Actualiza la física del juego a ~60 FPS
    private val actualizadorJuego = object : Runnable {
        override fun run() {
            if (contandoInicio) {
                // Contador de inicio (3, 2, 1)
                val tiempoActual = System.currentTimeMillis()
                if (ultimoTiempo == 0L) {
                    ultimoTiempo = tiempoActual
                }
                
                if (tiempoActual - ultimoTiempo >= 1000) {
                    contadorInicio--
                    
                    if (contadorInicio > 0) {
                        txtContadorInicio.text = contadorInicio.toString()
                        ultimoTiempo = tiempoActual
                    } else {
                        // ¡Iniciar el juego!
                        txtContadorInicio.text = getString(R.string.ya)
                        handler.postDelayed({
                            iniciarJuegoReal()
                        }, 700)
                        return
                    }
                }
                
                handler.postDelayed(this, 16)
            } else if (juegoActivo) {
                // Actualizar física del juego
                vistaJuego.actualizar()

                // Actualizar temporizador solo cada segundo
                val tiempoActual = System.currentTimeMillis()
                if (ultimoTiempo == 0L) {
                    ultimoTiempo = tiempoActual
                }
                
                if (tiempoActual - ultimoTiempo >= 1000) {
                    tiempoRestante--
                    actualizarTemporizador()
                    ultimoTiempo = tiempoActual
                    
                    // Verificar si se acabó el tiempo
                    if (tiempoRestante <= 0) {
                        terminarJuegoPorTiempo()
                        return
                    }
                }

                handler.postDelayed(this, 16) // ~60 FPS
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        // Ocultar ActionBar
        supportActionBar?.hide()

        // Obtener modo de juego
        modoUnJugador = intent.getBooleanExtra("MODO_UN_JUGADOR", false)

        inicializarVistas()
        configurarVistaJuego()
        configurarBotones()
    }

    /**
     * Inicializa todas las vistas
     */
    private fun inicializarVistas() {
        vistaJuego = findViewById(R.id.vistaJuego)
        txtPuntosJ1 = findViewById(R.id.txtPuntosJ1)
        txtPuntosJ2 = findViewById(R.id.txtPuntosJ2)
        txtNombreJ1 = findViewById(R.id.txtNombreJ1)
        txtNombreJ2 = findViewById(R.id.txtNombreJ2)
        txtTiempoJ1 = findViewById(R.id.txtTiempoJ1)
        txtTiempoJ2 = findViewById(R.id.txtTiempoJ2)
        txtContadorInicio = findViewById(R.id.txtContadorInicio)
        txtMensajeJ1 = findViewById(R.id.txtMensajeJ1)
        txtMensajeJ2 = findViewById(R.id.txtMensajeJ2)
        btnIniciar = findViewById(R.id.btnIniciar)
        btnPausarJ1 = findViewById(R.id.btnPausarJ1)
        btnPausarJ2 = findViewById(R.id.btnPausarJ2)

        // Configurar nombres según el modo
        if (modoUnJugador) {
            txtNombreJ2.text = getString(R.string.cpu)
        }

        actualizarMarcador()
        actualizarTemporizador()
    }

    /**
     * Configura los callbacks de la vista del juego
     */
    private fun configurarVistaJuego() {
        vistaJuego.modoUnJugador = modoUnJugador

        // Callback cuando se anota un gol (cada gol vale 1 punto)
        vistaJuego.alAnotarGol = { jugador ->
            if (jugador == 1) {
                puntosJ1 += 1 // Jugador 1 anota 1 punto
            } else {
                puntosJ2 += 1 // Jugador 2 anota 1 punto
            }
            actualizarMarcador()
        }

        // Callback cuando hay penalización
        vistaJuego.alPenalizacion = { jugador ->
            mostrarPenalizacion(jugador)
        }

        // Callback cuando ambos jugadores están listos
        vistaJuego.alAmbosJugadoresListos = {
            txtMensajeJ1.visibility = View.GONE
            txtMensajeJ2.visibility = View.GONE
            // Iniciar el contador de inicio (3, 2, 1)
            if (!temporizadorIniciado) {
                temporizadorIniciado = true
                iniciarContadorInicio()
            }
        }
    }

    /**
     * Configura los botones
     */
    private fun configurarBotones() {
        btnIniciar.setOnClickListener {
            iniciarJuego()
        }
        
        btnPausarJ1.setOnClickListener {
            pausarJuego()
        }
        
        btnPausarJ2.setOnClickListener {
            pausarJuego()
        }
    }

    /**
     * Inicia el juego (prepara el juego, pero el temporizador no inicia hasta que ambos estén listos)
     */
    private fun iniciarJuego() {
        vistaJuego.juegoIniciado = true
        vistaJuego.juegoPausado = false

        btnIniciar.visibility = View.GONE // Ocultar botón iniciar
        
        // Mostrar mensajes para ambos jugadores
        txtMensajeJ1.visibility = View.VISIBLE
        txtMensajeJ1.text = getString(R.string.coloca_dedo)
        txtMensajeJ2.visibility = View.VISIBLE
        txtMensajeJ2.text = getString(R.string.coloca_dedo)
        
        // El temporizador se iniciará cuando ambos jugadores coloquen sus dedos
    }

    /**
     * Inicia el contador de inicio (3, 2, 1, ¡YA!)
     */
    private fun iniciarContadorInicio() {
        contandoInicio = true
        contadorInicio = 3
        txtContadorInicio.text = "3"
        txtContadorInicio.visibility = View.VISIBLE
        ultimoTiempo = System.currentTimeMillis()
        handler.post(actualizadorJuego)
    }

    /**
     * Inicia el juego real después del contador
     */
    private fun iniciarJuegoReal() {
        contandoInicio = false
        txtContadorInicio.visibility = View.GONE
        
        // Mostrar temporizadores laterales
        txtTiempoJ1.visibility = View.VISIBLE
        txtTiempoJ2.visibility = View.VISIBLE
        
        // Mostrar botones de pausa laterales
        btnPausarJ1.visibility = View.VISIBLE
        btnPausarJ2.visibility = View.VISIBLE
        
        // Iniciar el juego
        juegoActivo = true
        ultimoTiempo = System.currentTimeMillis()
        handler.post(actualizadorJuego)
    }

    /**
     * Pausa el juego
     */
    private fun pausarJuego() {
        if (contandoInicio) {
            // No se puede pausar durante el contador inicial
            return
        }
        
        juegoActivo = false
        vistaJuego.juegoPausado = true
        ultimoTiempo = 0L // Reiniciar para evitar saltos de tiempo

        // Ocultar botones de pausa
        btnPausarJ1.visibility = View.GONE
        btnPausarJ2.visibility = View.GONE

        mostrarDialogoPausa()
    }

    /**
     * Muestra el diálogo de pausa
     */
    private fun mostrarDialogoPausa() {
        AlertDialog.Builder(this)
            .setTitle("Juego Pausado")
            .setMessage("¿Qué deseas hacer?")
            .setPositiveButton("Continuar") { dialog, _ ->
                continuarJuego()
                dialog.dismiss()
            }
            .setNegativeButton("Salir") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Continúa el juego después de la pausa
     */
    private fun continuarJuego() {
        // Iniciar contador de 3 segundos antes de continuar
        contandoInicio = true
        contadorInicio = 3
        txtContadorInicio.text = "3"
        txtContadorInicio.visibility = View.VISIBLE
        
        // Ocultar temporizadores temporalmente
        txtTiempoJ1.visibility = View.GONE
        txtTiempoJ2.visibility = View.GONE
        
        vistaJuego.juegoPausado = false
        ultimoTiempo = System.currentTimeMillis()
        handler.post(actualizadorJuego)
    }

    /**
     * Actualiza el marcador
     */
    private fun actualizarMarcador() {
        txtPuntosJ1.text = puntosJ1.toString()
        txtPuntosJ2.text = puntosJ2.toString()
    }

    /**
     * Actualiza el temporizador (ambos, J1 y J2)
     */
    private fun actualizarTemporizador() {
        val minutos = tiempoRestante / 60
        val segundos = tiempoRestante % 60
        val tiempo = String.format("%d:%02d", minutos, segundos)
        txtTiempoJ1.text = tiempo
        txtTiempoJ2.text = tiempo
    }

    /**
     * Muestra mensaje de penalización con diseño mejorado
     */
    private fun mostrarPenalizacion(jugador: Int) {
        juegoActivo = false
        handler.removeCallbacks(actualizadorJuego)
        
        // Ocultar botones de pausa
        btnPausarJ1.visibility = View.GONE
        btnPausarJ2.visibility = View.GONE

        val nombreJugador = if (jugador == 1) {
            txtNombreJ1.text
        } else {
            txtNombreJ2.text
        }
        
        // Crear vista personalizada para el diálogo
        val dialogView = layoutInflater.inflate(R.layout.dialog_penalizacion, null)
        
        val txtMensajePenalizacion = dialogView.findViewById<TextView>(R.id.txtMensajePenalizacion)
        val btnAceptarPenalizacion = dialogView.findViewById<MaterialButton>(R.id.btnAceptarPenalizacion)
        
        txtMensajePenalizacion.text = "$nombreJugador infringió las reglas\n\nEl juego ha terminado"
        
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        
        btnAceptarPenalizacion.setOnClickListener {
            dialog.dismiss()
            // El jugador penalizado pierde, el otro gana
            if (jugador == 1) {
                terminarJuego(ganador = 2)
            } else {
                terminarJuego(ganador = 1)
            }
        }
        
        dialog.show()
    }

    /**
     * Termina el juego por tiempo
     */
    private fun terminarJuegoPorTiempo() {
        juegoActivo = false
        handler.removeCallbacks(actualizadorJuego)

        val ganador = when {
            puntosJ1 > puntosJ2 -> 1
            puntosJ2 > puntosJ1 -> 2
            else -> 0 // Empate
        }

        terminarJuego(ganador)
    }

    /**
     * Termina el juego y muestra el resultado con diseño mejorado
     */
    private fun terminarJuego(ganador: Int) {
        // Crear vista personalizada para el diálogo
        val dialogView = layoutInflater.inflate(R.layout.dialog_fin_juego, null)
        
        val txtResultado = dialogView.findViewById<TextView>(R.id.txtResultado)
        val txtPuntosFinales = dialogView.findViewById<TextView>(R.id.txtPuntosFinales)
        val btnJugarDeNuevo = dialogView.findViewById<MaterialButton>(R.id.btnJugarDeNuevo)
        val btnMenuPrincipal = dialogView.findViewById<MaterialButton>(R.id.btnMenuPrincipal)
        
        // Configurar resultado
        when (ganador) {
            1 -> {
                txtResultado.text = "🏆 ¡${txtNombreJ1.text} GANA!"
                txtResultado.setTextColor(getColor(R.color.rojoJugador1))
            }
            2 -> {
                txtResultado.text = "🏆 ¡${txtNombreJ2.text} GANA!"
                txtResultado.setTextColor(getColor(R.color.azulJugador2))
            }
            else -> {
                txtResultado.text = "🤝 ¡EMPATE!"
                txtResultado.setTextColor(getColor(R.color.textoBlanco))
            }
        }
        
        txtPuntosFinales.text = "${txtNombreJ1.text}: $puntosJ1\n${txtNombreJ2.text}: $puntosJ2"
        
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        
        btnJugarDeNuevo.setOnClickListener {
            dialog.dismiss()
            reiniciarJuego()
        }
        
        btnMenuPrincipal.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        
        dialog.show()
    }
    
    /**
     * Reinicia el juego para jugar de nuevo
     */
    private fun reiniciarJuego() {
        puntosJ1 = 0
        puntosJ2 = 0
        tiempoRestante = 180
        temporizadorIniciado = false
        contandoInicio = false
        juegoActivo = false
        
        actualizarMarcador()
        actualizarTemporizador()
        
        vistaJuego.reiniciar()
        vistaJuego.juegoIniciado = false
        
        btnIniciar.visibility = View.VISIBLE
        btnIniciar.text = getString(R.string.btn_iniciar)
        btnPausarJ1.visibility = View.GONE
        btnPausarJ2.visibility = View.GONE
        txtTiempoJ1.visibility = View.GONE
        txtTiempoJ2.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        if (juegoActivo) {
            pausarJuego()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(actualizadorJuego)
    }
}

 