# Air Hockey - Juego Android

## 📱 Descripción

Juego de Air Hockey multitáctil para Android desarrollado en Kotlin. El juego incluye modo un jugador (vs CPU) y multijugador local, con sistema de puntuaciones guardadas.

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
- Duración de 3 minutos por partida
- Sistema de goles con detección automática

### Sistema de Puntuaciones
- Guardar puntuaciones al finalizar cada partida
- Top 10 mejores puntuaciones
- Persistencia de datos con SharedPreferences
- Visualización ordenada por puntos
- Diferenciación entre modos de juego

## 🏗️ Estructura del Proyecto

```
app/src/main/
├── java/com/example/examenhockey/
│   ├── MenuPrincipalActivity.kt     # Menú principal
│   ├── JuegoActivity.kt             # Lógica del juego y temporizador
│   ├── PuntuacionesActivity.kt      # Visualización de puntuaciones
│   ├── VistaJuego.kt                # Vista personalizada con física
│   ├── Puntuacion.kt                # Modelo de datos
│   └── GestorPuntuaciones.kt        # Persistencia de datos
│
├── res/
│   ├── layout/
│   │   ├── activity_menu_principal.xml
│   │   ├── activity_juego.xml
│   │   ├── activity_puntuaciones.xml
│   │   └── item_puntuacion.xml
│   │
│   ├── drawable/
│   │   ├── fondo_gradiente.xml      # Fondo degradado
│   │   └── circulo_posicion.xml     # Indicador de posición
│   │
│   └── values/
│       ├── colors.xml               # Paleta de colores
│       ├── strings.xml              # Textos del juego
│       └── themes.xml               # Temas de la app
```

## 🎯 Reglas del Juego

1. El objetivo es anotar la mayor cantidad de goles en la portería del rival
2. Cada jugador controla una paleta tocando su mitad de la pantalla
3. El juego no inicia hasta que ambos jugadores coloquen su dedo cerca de su portería
4. Restricciones:
   - No se puede cruzar la línea central
   - No se puede levantar el dedo durante el juego
5. El juego termina:
   - Por tiempo (3 minutos)
   - Por penalización de un jugador
6. Gana quien tenga más puntos al finalizar

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **SDK mínimo**: Android 10 (API 29)
- **SDK objetivo**: Android 14 (API 34)
- **Librerías**:
  - AndroidX Core KTX
  - Material Design Components
  - Gson (para serialización de datos)

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
- Lógica de física del juego
- Detección de colisiones
- Sistema de penalizaciones
- Gestión de puntuaciones

## 🎓 Características Técnicas Destacadas

### VistaJuego.kt
- Custom View con renderizado manual usando Canvas
- Sistema de física con velocidad, fricción y rebotes
- Detección multi-touch para 2 jugadores simultáneos
- IA simple para modo un jugador
- Callbacks para eventos del juego

### JuegoActivity.kt
- Temporizador con Handler y Runnable
- Actualización del juego a ~60 FPS
- Gestión de estados (pausado, activo, terminado)
- Diálogos para interacción con el usuario

### GestorPuntuaciones.kt
- Persistencia con SharedPreferences
- Serialización JSON con Gson
- Top 10 puntuaciones ordenadas
- Manejo de errores

## 👨‍💻 Autor

Proyecto desarrollado como examen de programación Android.

## 📄 Licencia

Este proyecto es de uso educativo.

