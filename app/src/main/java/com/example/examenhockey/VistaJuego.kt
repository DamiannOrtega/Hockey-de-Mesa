package com.example.examenhockey

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

/**
 * Vista personalizada que renderiza y maneja la física del juego de Air Hockey
 */
class VistaJuego @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Colores
    private val colorCancha = Color.parseColor("#0A2647")
    private val colorLinea = Color.WHITE
    private val colorDisco = Color.parseColor("#FFEB3B")
    private val colorPaletaJ1 = Color.parseColor("#E53935")
    private val colorPaletaJ2 = Color.parseColor("#1E88E5")
    
    // Pinturas
    private val pintCancha = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pintLinea = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pintDisco = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pintPaletaJ1 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pintPaletaJ2 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pintPorteria = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // Dimensiones
    private var anchoPantalla = 0f
    private var altoPantalla = 0f
    private var radioPorteria = 0f
    private var radioPaleta = 0f
    private var radioDisco = 0f
    
    // Posiciones del disco
    private var discoX = 0f
    private var discoY = 0f
    private var discoVelX = 0f
    private var discoVelY = 0f
    
    // Posiciones de las paletas
    private var paletaJ1X = 0f
    private var paletaJ1Y = 0f
    private var paletaJ2X = 0f
    private var paletaJ2Y = 0f
    
    // Control de jugadores
    private var j1Activo = false
    private var j2Activo = false
    private var idToqueJ1 = -1
    private var idToqueJ2 = -1
    
    // Sistema de penalización con tiempo de gracia
    private var tiempoSoltadoJ1 = 0L
    private var tiempoSoltadoJ2 = 0L
    private val tiempoGraciaPenalizacion = 2500L // 2.5 segundos
    
    // Callbacks
    var alAnotarGol: ((jugador: Int) -> Unit)? = null
    var alPenalizacion: ((jugador: Int) -> Unit)? = null
    var alAmbosJugadoresListos: (() -> Unit)? = null
    
    // Estado del juego
    var juegoIniciado = false
    var juegoPausado = false
    var modoUnJugador = false
    
    // Animación de gol
    private var mostrandoGol = false
    private var tiempoGol = 0L
    private var jugadorQueAnoto = 0
    private val duracionAnimacionGol = 1500L // 1.5 segundos
    private var golYaContado = false // Bandera para evitar contar el gol múltiples veces
    
    // Física - Configuración estilo Glow Hockey (mucho rebote)
    private val friccion = 0.995f // Muy poca fricción (antes 0.98)
    private val rebote = 0.95f // Alto rebote (antes 0.85)
    private val velMaxDisco = 50f // Velocidad máxima aumentada
    private val velMinDisco = 3f // Velocidad mínima para mantener movimiento
    
    init {
        configurarPinturas()
        // Habilitar capas de hardware para sombras
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
    
    private fun configurarPinturas() {
        pintCancha.color = colorCancha
        pintCancha.style = Paint.Style.FILL
        
        pintLinea.color = colorLinea
        pintLinea.style = Paint.Style.STROKE
        pintLinea.strokeWidth = 5f
        
        pintDisco.color = colorDisco
        pintDisco.style = Paint.Style.FILL
        pintDisco.setShadowLayer(20f, 0f, 0f, Color.parseColor("#CCFFFFFF"))
        
        pintPaletaJ1.color = colorPaletaJ1
        pintPaletaJ1.style = Paint.Style.FILL
        pintPaletaJ1.setShadowLayer(10f, 0f, 0f, Color.parseColor("#80000000"))
        
        pintPaletaJ2.color = colorPaletaJ2
        pintPaletaJ2.style = Paint.Style.FILL
        pintPaletaJ2.setShadowLayer(10f, 0f, 0f, Color.parseColor("#80000000"))
        
        pintPorteria.color = colorLinea
        pintPorteria.style = Paint.Style.STROKE
        pintPorteria.strokeWidth = 8f
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        anchoPantalla = w.toFloat()
        altoPantalla = h.toFloat()
        
        // Calcular dimensiones
        radioPorteria = anchoPantalla * 0.2f
        radioPaleta = anchoPantalla * 0.09f
        radioDisco = anchoPantalla * 0.04f
        
        // Posiciones iniciales
        reiniciarPosiciones()
    }
    
    /**
     * Reinicia las posiciones del disco y paletas
     */
    fun reiniciarPosiciones() {
        discoX = anchoPantalla / 2
        discoY = altoPantalla / 2
        
        // Impulso inicial aleatorio más fuerte
        val anguloAleatorio = (Math.random() * 360).toFloat() * (Math.PI.toFloat() / 180f)
        val velocidadInicial = 20f // Aumentada de 15 a 20
        discoVelX = cos(anguloAleatorio) * velocidadInicial
        discoVelY = sin(anguloAleatorio) * velocidadInicial
        
        paletaJ1X = anchoPantalla / 2
        paletaJ1Y = altoPantalla * 0.75f
        
        paletaJ2X = anchoPantalla / 2
        paletaJ2Y = altoPantalla * 0.25f
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Dibujar cancha
        canvas.drawRect(0f, 0f, anchoPantalla, altoPantalla, pintCancha)
        
        // Dibujar línea central
        canvas.drawLine(0f, altoPantalla / 2, anchoPantalla, altoPantalla / 2, pintLinea)
        
        // Dibujar círculo central
        canvas.drawCircle(anchoPantalla / 2, altoPantalla / 2, anchoPantalla * 0.15f, pintLinea)
        
        // Dibujar porterías
        dibujarPorteria(canvas, altoPantalla * 0.05f, true) // Portería superior (J2)
        dibujarPorteria(canvas, altoPantalla * 0.95f, false) // Portería inferior (J1)
        
        // Dibujar disco
        canvas.drawCircle(discoX, discoY, radioDisco, pintDisco)
        
        // Dibujar paletas solo si los jugadores están activos
        if (j1Activo) {
            canvas.drawCircle(paletaJ1X, paletaJ1Y, radioPaleta, pintPaletaJ1)
        }
        if (j2Activo || modoUnJugador) {
            canvas.drawCircle(paletaJ2X, paletaJ2Y, radioPaleta, pintPaletaJ2)
        }
        
        // Dibujar animación de gol si es necesario
        if (mostrandoGol) {
            dibujarAnimacionGol(canvas)
        }
    }
    
    /**
     * Dibuja la animación cuando alguien anota un gol
     */
    private fun dibujarAnimacionGol(canvas: Canvas) {
        val tiempoTranscurrido = System.currentTimeMillis() - tiempoGol
        val progreso = (tiempoTranscurrido.toFloat() / duracionAnimacionGol.toFloat()).coerceIn(0f, 1f)
        
        if (tiempoTranscurrido > duracionAnimacionGol) {
            mostrandoGol = false
            return
        }
        
        val colorGol = if (jugadorQueAnoto == 1) colorPaletaJ1 else colorPaletaJ2
        val centroY = if (jugadorQueAnoto == 1) altoPantalla * 0.05f else altoPantalla * 0.95f
        
        // Flash de pantalla completa al inicio
        if (progreso < 0.2f) {
            val alphaFlash = ((0.2f - progreso) / 0.2f * 120).toInt()
            val pintFlash = Paint().apply {
                color = colorGol
                alpha = alphaFlash
            }
            canvas.drawRect(0f, 0f, anchoPantalla, altoPantalla, pintFlash)
        }
        
        // Círculos expansivos concéntricos
        for (i in 0..2) {
            val desfase = i * 0.15f
            val progresoCirculo = (progreso - desfase).coerceIn(0f, 1f)
            val alpha = ((1f - progresoCirculo) * 150).toInt()
            
            val pintCirculo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = colorGol
                this.alpha = alpha
                style = Paint.Style.STROKE
                strokeWidth = 15f
            }
            
            val radio = anchoPantalla * progresoCirculo * 1.2f
            canvas.drawCircle(anchoPantalla / 2, centroY, radio, pintCirculo)
        }
        
        // Partículas explosivas
        val numParticulas = 12
        for (i in 0 until numParticulas) {
            val angulo = (360f / numParticulas) * i
            val anguloRad = angulo * Math.PI.toFloat() / 180f
            val distancia = anchoPantalla * 0.3f * progreso
            val x = anchoPantalla / 2 + cos(anguloRad) * distancia
            val y = centroY + sin(anguloRad) * distancia
            
            val alphaParticula = ((1f - progreso) * 200).toInt()
            val pintParticula = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (i % 2 == 0) colorGol else Color.WHITE
                alpha = alphaParticula
            }
            
            val radioParticula = radioDisco * (1.5f - progreso * 0.5f)
            canvas.drawCircle(x, y, radioParticula, pintParticula)
        }
        
        // Texto "¡GOL!" con efecto de rebote
        val escalaTexto = if (progreso < 0.3f) {
            1f + (0.3f - progreso) * 2f // Rebote inicial
        } else {
            1f + progreso * 0.3f // Crecimiento gradual
        }
        
        val pintTextoGol = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (anchoPantalla * 0.15f) * escalaTexto
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 8f
            this.alpha = ((1f - progreso) * 255).toInt()
            setShadowLayer(25f, 0f, 0f, colorGol)
        }
        
        val textoPosY = if (jugadorQueAnoto == 1) {
            altoPantalla * 0.35f // Texto arriba si J1 anotó
        } else {
            altoPantalla * 0.65f // Texto abajo si J2 anotó
        }
        
        // Rotar el texto según el jugador que anotó
        canvas.save()
        if (jugadorQueAnoto == 2) {
            // Si anotó Jugador 2 (de arriba), rotar 180° para que lo vea de su lado
            canvas.rotate(180f, anchoPantalla / 2, textoPosY)
        }
        canvas.drawText("¡GOL!", anchoPantalla / 2, textoPosY, pintTextoGol)
        canvas.restore()
        
        // Líneas de velocidad
        for (i in 0..3) {
            val alphaLinea = ((1f - progreso) * 100).toInt()
            val pintLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                alpha = alphaLinea
                strokeWidth = 5f
                style = Paint.Style.STROKE
            }
            
            val offset = i * 40f
            val startY = if (jugadorQueAnoto == 1) centroY + offset else centroY - offset
            val endY = startY + (if (jugadorQueAnoto == 1) 80f else -80f) * progreso
            
            canvas.drawLine(anchoPantalla * 0.3f, startY, anchoPantalla * 0.3f, endY, pintLinea)
            canvas.drawLine(anchoPantalla * 0.7f, startY, anchoPantalla * 0.7f, endY, pintLinea)
        }
    }
    
    /**
     * Dibuja una portería
     */
    private fun dibujarPorteria(canvas: Canvas, y: Float, esArriba: Boolean) {
        val centroX = anchoPantalla / 2
        val startAngle = if (esArriba) 0f else 180f
        val sweepAngle = 180f
        
        val rect = RectF(
            centroX - radioPorteria,
            y - radioPorteria,
            centroX + radioPorteria,
            y + radioPorteria
        )
        
        canvas.drawArc(rect, startAngle, sweepAngle, false, pintPorteria)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!juegoIniciado || juegoPausado) return true
        
        val accion = event.actionMasked
        val indice = event.actionIndex
        val id = event.getPointerId(indice)
        
        when (accion) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val x = event.getX(indice)
                val y = event.getY(indice)
                
                // Determinar qué jugador toca
                if (y > altoPantalla / 2) {
                    // Zona del jugador 1 (inferior)
                    if (!j1Activo) {
                        j1Activo = true
                        idToqueJ1 = id
                        paletaJ1X = x
                        paletaJ1Y = y
                        verificarAmbosListos()
                    }
                } else {
                    // Zona del jugador 2 (superior)
                    if (!j2Activo && !modoUnJugador) {
                        j2Activo = true
                        idToqueJ2 = id
                        paletaJ2X = x
                        paletaJ2Y = y
                        verificarAmbosListos()
                    }
                }
                
                // Cancelar penalización si vuelve a tocar dentro del tiempo de gracia
                if (id == idToqueJ1) {
                    tiempoSoltadoJ1 = 0L
                }
                if (id == idToqueJ2) {
                    tiempoSoltadoJ2 = 0L
                }
            }
            
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val punteroId = event.getPointerId(i)
                    val x = event.getX(i)
                    val y = event.getY(i)
                    
                    when (punteroId) {
                        idToqueJ1 -> {
                            // Verificar que no pase la mitad de la cancha
                            if (y <= altoPantalla / 2) {
                                penalizar(1)
                                return true
                            }
                            
                            val anteriorX = paletaJ1X
                            val anteriorY = paletaJ1Y
                            
                            paletaJ1X = x.coerceIn(radioPaleta, anchoPantalla - radioPaleta)
                            paletaJ1Y = y.coerceIn(altoPantalla / 2 + radioPaleta, altoPantalla - radioPaleta)
                            
                            // Verificar colisión con disco
                            verificarColisionPaleta(paletaJ1X, paletaJ1Y, anteriorX, anteriorY)
                        }
                        
                        idToqueJ2 -> {
                            // Verificar que no pase la mitad de la cancha
                            if (y >= altoPantalla / 2) {
                                penalizar(2)
                                return true
                            }
                            
                            val anteriorX = paletaJ2X
                            val anteriorY = paletaJ2Y
                            
                            paletaJ2X = x.coerceIn(radioPaleta, anchoPantalla - radioPaleta)
                            paletaJ2Y = y.coerceIn(radioPaleta, altoPantalla / 2 - radioPaleta)
                            
                            // Verificar colisión con disco
                            verificarColisionPaleta(paletaJ2X, paletaJ2Y, anteriorX, anteriorY)
                        }
                    }
                }
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val punteroId = event.getPointerId(indice)
                
                when (punteroId) {
                    idToqueJ1 -> {
                        // Registrar el tiempo cuando el jugador suelta el dedo
                        tiempoSoltadoJ1 = System.currentTimeMillis()
                        // No penalizar inmediatamente, darle 2.5 segundos de gracia
                    }
                    idToqueJ2 -> {
                        // Registrar el tiempo cuando el jugador suelta el dedo
                        tiempoSoltadoJ2 = System.currentTimeMillis()
                        // No penalizar inmediatamente, darle 2.5 segundos de gracia
                    }
                }
            }
        }
        
        return true
    }
    
    /**
     * Verifica si ambos jugadores están listos
     */
    private fun verificarAmbosListos() {
        if ((j1Activo && j2Activo) || (j1Activo && modoUnJugador)) {
            alAmbosJugadoresListos?.invoke()
        }
    }
    
    /**
     * Penaliza a un jugador
     */
    private fun penalizar(jugador: Int) {
        alPenalizacion?.invoke(jugador)
    }
    
    /**
     * Verifica colisión entre paleta y disco
     */
    private fun verificarColisionPaleta(px: Float, py: Float, anteriorX: Float, anteriorY: Float) {
        val distancia = distancia(px, py, discoX, discoY)
        
        if (distancia < radioPaleta + radioDisco) {
            // Hay colisión - calcular velocidad del disco
            val angulo = atan2(discoY - py, discoX - px)
            
            // Velocidad de la paleta
            val velPaletaX = px - anteriorX
            val velPaletaY = py - anteriorY
            
            // Aplicar impulso al disco (aumentado para más acción)
            val impulso = sqrt(velPaletaX * velPaletaX + velPaletaY * velPaletaY) * 0.8f // Aumentado de 0.5 a 0.8
            
            discoVelX = cos(angulo) * impulso + velPaletaX * 0.5f // Aumentado de 0.3 a 0.5
            discoVelY = sin(angulo) * impulso + velPaletaY * 0.5f
            
            // Limitar velocidad máxima
            val velocidad = sqrt(discoVelX * discoVelX + discoVelY * discoVelY)
            if (velocidad > velMaxDisco) {
                discoVelX = (discoVelX / velocidad) * velMaxDisco
                discoVelY = (discoVelY / velocidad) * velMaxDisco
            }
            
            // Separar disco de paleta
            val separacion = radioPaleta + radioDisco
            discoX = px + cos(angulo) * separacion
            discoY = py + sin(angulo) * separacion
        }
    }
    
    /**
     * Actualiza la física del juego
     */
    fun actualizar() {
        if (!juegoIniciado || juegoPausado) return
        
        // Verificar penalizaciones por tiempo
        verificarPenalizacionesPorTiempo()
        
        // Actualizar IA si es modo un jugador
        if (modoUnJugador) {
            actualizarIA()
        }
        
        // Actualizar posición del disco
        discoX += discoVelX
        discoY += discoVelY
        
        // Aplicar fricción muy baja (disco casi siempre en movimiento)
        discoVelX *= friccion
        discoVelY *= friccion
        
        // Mantener velocidad mínima para que siempre esté rebotando
        val velocidadActual = sqrt(discoVelX * discoVelX + discoVelY * discoVelY)
        
        if (velocidadActual > 0.5f && velocidadActual < velMinDisco) {
            // Si va muy lento pero no se ha detenido, darle un pequeño impulso
            val factor = velMinDisco / velocidadActual
            discoVelX *= factor
            discoVelY *= factor
        } else if (velocidadActual <= 0.5f) {
            // Solo detener si está prácticamente quieto
            discoVelX = 0f
            discoVelY = 0f
        }
        
        // Verificar colisiones con paredes
        verificarColisionesParedes()
        
        // Verificar goles
        verificarGoles()
        
        // Siempre invalidar para que se siga dibujando la animación de gol
        invalidate()
    }
    
    /**
     * Verifica si algún jugador ha soltado la pantalla por más de 2.5 segundos
     */
    private fun verificarPenalizacionesPorTiempo() {
        val tiempoActual = System.currentTimeMillis()
        
        // Verificar Jugador 1
        if (tiempoSoltadoJ1 > 0L && (tiempoActual - tiempoSoltadoJ1) >= tiempoGraciaPenalizacion) {
            penalizar(1)
            tiempoSoltadoJ1 = 0L // Resetear para evitar múltiples penalizaciones
        }
        
        // Verificar Jugador 2 (solo si no es modo un jugador)
        if (!modoUnJugador && tiempoSoltadoJ2 > 0L && (tiempoActual - tiempoSoltadoJ2) >= tiempoGraciaPenalizacion) {
            penalizar(2)
            tiempoSoltadoJ2 = 0L // Resetear para evitar múltiples penalizaciones
        }
    }
    
    /**
     * IA mejorada para el modo un jugador
     */
    private fun actualizarIA() {
        j2Activo = true
        
        // Guardar posición anterior para calcular velocidad
        val anteriorX = paletaJ2X
        val anteriorY = paletaJ2Y
        
        // Velocidad de la IA
        val velocidadIA = 12f
        val velocidadAtaque = 15f
        
        // Calcular distancia al disco
        val distAlDisco = distancia(paletaJ2X, paletaJ2Y, discoX, discoY)
        
        if (discoY < altoPantalla / 2 && discoVelY >= 0) {
            // MODO ATAQUE: El disco está en su mitad y va hacia el jugador
            // Intentar golpear el disco hacia la portería del jugador
            
            // Calcular punto de intercepción más agresivo
            val objetivoX: Float
            val objetivoY: Float
            
            if (distAlDisco < radioPaleta * 4) {
                // Cerca del disco - ir directo a él
                objetivoX = discoX
                objetivoY = discoY
            } else {
                // Lejos del disco - posicionarse estratégicamente
                objetivoX = discoX
                objetivoY = discoY - radioPaleta
            }
            
            // Mover hacia el objetivo con velocidad de ataque
            val difX = objetivoX - paletaJ2X
            val difY = objetivoY - paletaJ2Y
            
            if (abs(difX) > 3f) {
                paletaJ2X += if (difX > 0) velocidadAtaque else -velocidadAtaque
            }
            
            if (abs(difY) > 3f) {
                paletaJ2Y += if (difY > 0) velocidadAtaque else -velocidadAtaque
            }
            
        } else if (discoY < altoPantalla / 2 && discoVelY < 0) {
            // MODO DEFENSA: El disco viene hacia la CPU
            // Posicionarse para bloquear
            
            val objetivoX = discoX
            val objetivoY = altoPantalla * 0.15f
            
            val difX = objetivoX - paletaJ2X
            val difY = objetivoY - paletaJ2Y
            
            if (abs(difX) > 5f) {
                paletaJ2X += if (difX > 0) velocidadIA else -velocidadIA
            }
            
            if (abs(difY) > 5f) {
                paletaJ2Y += if (difY > 0) velocidadIA else -velocidadIA
            }
            
        } else {
            // MODO RETORNO: El disco está en la mitad del jugador
            // Volver a posición defensiva estratégica
            
            val centroX = anchoPantalla / 2
            val centroY = altoPantalla * 0.2f
            
            // Evitar quedarse atorado en las esquinas
            val margen = radioPaleta * 2
            val limiteIzq = margen
            val limiteDer = anchoPantalla - margen
            
            var objetivoX = centroX
            
            // Si está muy cerca de una esquina, moverse hacia el centro
            if (paletaJ2X < limiteIzq) {
                objetivoX = anchoPantalla * 0.3f
            } else if (paletaJ2X > limiteDer) {
                objetivoX = anchoPantalla * 0.7f
            } else {
                // Anticipar el regreso del disco
                if (discoVelY < 0 && discoY > altoPantalla * 0.6f) {
                    objetivoX = discoX // Anticiparse
                }
            }
            
            if (abs(paletaJ2X - objetivoX) > 5f) {
                paletaJ2X += if (objetivoX > paletaJ2X) velocidadIA else -velocidadIA
            }
            if (abs(paletaJ2Y - centroY) > 5f) {
                paletaJ2Y += if (centroY > paletaJ2Y) velocidadIA else -velocidadIA
            }
        }
        
        // Limitar posición de la IA
        paletaJ2X = paletaJ2X.coerceIn(radioPaleta * 1.5f, anchoPantalla - radioPaleta * 1.5f)
        paletaJ2Y = paletaJ2Y.coerceIn(radioPaleta * 1.5f, altoPantalla / 2 - radioPaleta * 1.5f)
        
        // Verificar colisión con disco (con velocidad calculada)
        verificarColisionPaleta(paletaJ2X, paletaJ2Y, anteriorX, anteriorY)
    }
    
    /**
     * Verifica colisiones del disco con las paredes
     */
    private fun verificarColisionesParedes() {
        // Paredes laterales
        if (discoX - radioDisco < 0) {
            discoX = radioDisco
            discoVelX = -discoVelX * rebote
        } else if (discoX + radioDisco > anchoPantalla) {
            discoX = anchoPantalla - radioDisco
            discoVelX = -discoVelX * rebote
        }
        
        // Paredes superior e inferior (excepto portería)
        val centroX = anchoPantalla / 2
        
        // Pared superior
        if (discoY - radioDisco < 0) {
            val distPorteria = abs(discoX - centroX)
            if (distPorteria > radioPorteria) {
                discoY = radioDisco
                discoVelY = -discoVelY * rebote
            }
        }
        
        // Pared inferior
        if (discoY + radioDisco > altoPantalla) {
            val distPorteria = abs(discoX - centroX)
            if (distPorteria > radioPorteria) {
                discoY = altoPantalla - radioDisco
                discoVelY = -discoVelY * rebote
            }
        }
    }
    
    /**
     * Verifica si se anotó un gol
     */
    private fun verificarGoles() {
        val centroX = anchoPantalla / 2
        val distPorteria = abs(discoX - centroX)
        
        // Gol en portería superior (J2 pierde, J1 anota)
        if (discoY - radioDisco < 0 && distPorteria <= radioPorteria && !golYaContado) {
            // Marcar que el gol ya fue contado para evitar múltiples llamadas
            golYaContado = true
            
            // Activar animación de gol
            mostrandoGol = true
            tiempoGol = System.currentTimeMillis()
            jugadorQueAnoto = 1
            
            // Invocar callback UNA SOLA VEZ
            alAnotarGol?.invoke(1)
            
            // Reiniciar posiciones después de un pequeño delay para que se vea la animación
            postDelayed({
                reiniciarPosiciones()
                golYaContado = false // Resetear bandera para el próximo gol
            }, 500)
        }
        
        // Gol en portería inferior (J1 pierde, J2 anota)
        if (discoY + radioDisco > altoPantalla && distPorteria <= radioPorteria && !golYaContado) {
            // Marcar que el gol ya fue contado para evitar múltiples llamadas
            golYaContado = true
            
            // Activar animación de gol
            mostrandoGol = true
            tiempoGol = System.currentTimeMillis()
            jugadorQueAnoto = 2
            
            // Invocar callback UNA SOLA VEZ
            alAnotarGol?.invoke(2)
            
            // Reiniciar posiciones después de un pequeño delay para que se vea la animación
            postDelayed({
                reiniciarPosiciones()
                golYaContado = false // Resetear bandera para el próximo gol
            }, 500)
        }
    }
    
    /**
     * Calcula la distancia entre dos puntos
     */
    private fun distancia(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    }
    
    /**
     * Reinicia el estado del juego
     */
    fun reiniciar() {
        j1Activo = false
        j2Activo = false
        idToqueJ1 = -1
        idToqueJ2 = -1
        tiempoSoltadoJ1 = 0L
        tiempoSoltadoJ2 = 0L
        reiniciarPosiciones()
        invalidate()
    }
}

