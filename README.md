# Air Hockey - Juego Android

## 📱 Descripción

Juego de Air Hockey multitáctil para Android desarrollado en Kotlin. El juego incluye modo un jugador (vs CPU) y multijugador local con física realista y controles táctiles intuitivos.

## 🎮 Características

### Modos de Juego
- **Un Jugador**: Juega contra una IA
- **Multijugador**: Juega con otra persona en el mismo dispositivo

### Mecánicas del Juego
- Control táctil intuitivo con paletas para cada jugador
- Física realista del disco con fricción y rebotes
- Detección de colisiones precisa
- Sistema de penalizaciones:
  - Levantar el dedo durante el juego
  - Cruzar la mitad de la cancha
- Duración de 2 minutos por partida
- Sistema de goles con detección automática
- Efectos visuales espectaculares al anotar
- Contador de inicio (3, 2, 1, ¡YA!)
- Diálogos modernos para finalización

## 🏗️ Estructura del Proyecto

```
app/src/main/
├── java/com/example/examenhockey/
│   ├── MenuPrincipalActivity.kt     # Menú principal con logo
│   ├── JuegoActivity.kt             # Lógica del juego y temporizador
│   └── VistaJuego.kt                # Vista personalizada con física e IA
│
├── res/
│   ├── layout/
│   │   ├── activity_menu_principal.xml
│   │   ├── activity_juego.xml
│   │   ├── dialog_fin_juego.xml      # Diálogo de finalización
│   │   └── dialog_penalizacion.xml   # Diálogo de penalización
│   │
│   ├── drawable/
│   │   ├── fondo_gradiente.xml       # Fondo degradado
│   │   └── logo.png                  # Logo de la aplicación
│   │
│   └── values/
│       ├── colors.xml                # Paleta de colores moderna
│       ├── strings.xml               # Textos del juego
│       └── themes.xml                # Material Design 3
```

## 🎯 Reglas del Juego

1. El objetivo es anotar la mayor cantidad de goles en la portería del rival
2. Cada jugador controla una paleta tocando su mitad de la pantalla
3. El juego no inicia hasta que ambos jugadores coloquen su dedo cerca de su portería
4. Un contador de 3 segundos (3, 2, 1, ¡YA!) precede al inicio del juego
5. Restricciones:
   - No se puede cruzar la línea central
   - Levantar el dedo por más de 2.5 segundos resulta en penalización
6. El juego termina:
   - Por tiempo (2 minutos)
   - Por penalización de un jugador
7. Gana quien tenga más puntos al finalizar
8. Cada gol vale exactamente 1 punto

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **SDK mínimo**: Android 10 (API 29)
- **SDK compilación**: Android 14 (API 36)
- **Librerías**:
  - AndroidX Core KTX
  - Material Design 3 Components
  - AndroidX CardView

## 🎨 Diseño

- Interfaz moderna con Material Design 3
- Paleta de colores azul y rojo para diferenciación de jugadores
- Gradientes y sombras para mejor experiencia visual
- Diseño responsive adaptado a diferentes tamaños de pantalla
- Modo portrait (vertical) forzado para mejor experiencia

## 🚀 Cómo Compilar

1. Abre el proyecto en Android Studio
2. Sincroniza Gradle
3. Conecta un dispositivo Android o inicia un emulador
4. Ejecuta la aplicación

## 📝 Comentarios en el Código

El código está comentado en español en las secciones importantes para facilitar su comprensión:
- Lógica de física del juego con fricción y rebotes
- Detección de colisiones entre disco y paletas
- Sistema de penalizaciones con período de gracia
- Algoritmos de inteligencia artificial
- Efectos visuales y animaciones

## 🎓 Características Técnicas Destacadas

### VistaJuego.kt (742 líneas)
- Custom View con renderizado manual usando Canvas a 60 FPS
- Sistema de física avanzado con velocidad, fricción y rebotes
- Detección multi-touch para 2 jugadores simultáneos
- IA inteligente con modos ataque/defensa/retorno
- Sistema anti-atascamiento para la CPU
- Efectos visuales de gol con animaciones múltiples
- Callbacks para eventos del juego
- Partículas explosivas y ondas expansivas
- Sombras y efectos de brillo para elementos

### JuegoActivity.kt (464 líneas)
- Temporizador dual visible para ambos jugadores
- Contador de inicio de 3 segundos
- Actualización del juego a 60 FPS constantes
- Gestión de estados (pausado, activo, terminado)
- Diálogos personalizados con layouts modernos
- Período de gracia de 2.5 segundos para penalizaciones
- Sistema de reinicio sin recrear Activity
- Botones de pausa duales (uno por jugador)

## 👨‍💻 Autor

**Juan Damián Ortega De Luna**

Proyecto desarrollado como examen de programación Android con Kotlin.

## 📄 Licencia

Este proyecto es de uso educativo.

