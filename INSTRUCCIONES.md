# 📋 Instrucciones de Instalación y Uso

## 🔧 Requisitos Previos

- Android Studio (versión recomendada: Hedgehog o superior)
- JDK 11 o superior
- Dispositivo Android o Emulador con API 29+ (Android 10+)

## 📥 Instalación

### 1. Abrir el Proyecto

1. Abre Android Studio
2. Selecciona "Open" o "Abrir proyecto existente"
3. Navega a la carpeta `ExamenHockey`
4. Haz clic en "OK"

### 2. Sincronizar Gradle

1. Espera a que Android Studio indexe el proyecto
2. Haz clic en "Sync Now" si aparece el mensaje de sincronización
3. Espera a que Gradle descargue todas las dependencias

### 3. Compilar el Proyecto

#### Opción A: Desde Android Studio
1. Ve a `Build` > `Make Project` (o presiona `Ctrl+F9`)
2. Espera a que la compilación termine sin errores

#### Opción B: Desde Terminal
```bash
./gradlew build
```

## 🚀 Ejecutar la Aplicación

### En un Dispositivo Físico

1. Habilita "Opciones de Desarrollador" en tu dispositivo Android:
   - Ve a `Ajustes` > `Acerca del teléfono`
   - Toca 7 veces en "Número de compilación"
   
2. Habilita "Depuración USB":
   - Ve a `Ajustes` > `Opciones de desarrollador`
   - Activa "Depuración USB"

3. Conecta tu dispositivo al ordenador con un cable USB

4. En Android Studio:
   - Selecciona tu dispositivo en el menú desplegable superior
   - Haz clic en el botón "Run" (▶️) o presiona `Shift+F10`

### En un Emulador

1. Crea un AVD (Android Virtual Device):
   - Ve a `Tools` > `Device Manager`
   - Haz clic en "Create Device"
   - Selecciona un dispositivo (recomendado: Pixel 5 o similar)
   - Selecciona una imagen del sistema (API 29 o superior)
   - Haz clic en "Finish"

2. Inicia el emulador:
   - Selecciona el AVD creado
   - Haz clic en el botón "Run" (▶️)

## 🎮 Cómo Jugar

### Menú Principal

Al iniciar la app verás 4 opciones:

1. **Un Jugador**: Juega contra la CPU (Inteligencia Artificial)
2. **Multijugador**: Juega con otra persona en el mismo dispositivo
3. **Puntuaciones**: Ver las mejores puntuaciones guardadas
4. **Salir**: Cerrar la aplicación

### Durante el Juego

#### Inicio
1. Presiona el botón "INICIAR"
2. Ambos jugadores deben colocar su dedo en su mitad de la pantalla
3. Una vez detectados ambos dedos, el juego comienza

#### Controles
- **Jugador 1** (Rojo): Controla la mitad inferior de la pantalla
- **Jugador 2** (Azul): Controla la mitad superior de la pantalla
- Desliza tu dedo para mover tu paleta
- Golpea el disco amarillo para enviarlo a la portería rival

#### Reglas Importantes
- ⚠️ **NO levantes tu dedo** durante el juego (penalización)
- ⚠️ **NO cruces la línea central** (penalización)
- 🎯 Anota goles en la portería del rival
- ⏱️ El juego dura **3 minutos**

### Puntuaciones

- Al finalizar un juego, se te pedirá tu nombre
- Tu puntuación se guardará automáticamente
- Solo se guardan las **Top 10** mejores puntuaciones
- Las puntuaciones muestran:
  - 🥇 Posición (Oro, Plata, Bronce para los 3 primeros)
  - 👤 Nombre del jugador
  - 🎮 Modo de juego
  - 📅 Fecha y hora
  - 🏆 Puntos obtenidos

## 🐛 Solución de Problemas

### Error al compilar

**Problema**: "Unsupported compileSdk version"
- **Solución**: Asegúrate de tener instalado el SDK de Android API 34
  - Ve a `Tools` > `SDK Manager`
  - Instala "Android 14.0 (API 34)"

**Problema**: "Failed to resolve: com.google.code.gson:gson"
- **Solución**: Sincroniza Gradle nuevamente
  - Haz clic en `File` > `Sync Project with Gradle Files`

### Error al ejecutar

**Problema**: "Installation failed with message INSTALL_FAILED_OLDER_SDK"
- **Solución**: Tu dispositivo debe tener Android 10 (API 29) o superior

**Problema**: El juego no detecta los toques
- **Solución**: 
  - Asegúrate de estar en modo vertical (portrait)
  - Reinicia la aplicación
  - Verifica que hayas presionado "INICIAR"

### Problemas de rendimiento

**Problema**: El juego va lento o con lag
- **Solución**:
  - Cierra otras aplicaciones
  - Si es en emulador, asigna más RAM al AVD
  - Prueba en un dispositivo físico

## 📱 Características del Dispositivo Recomendadas

- **OS**: Android 10 o superior
- **RAM**: 2 GB o más
- **Pantalla**: 5" o superior para mejor experiencia multijugador
- **Multi-touch**: Soporte para al menos 2 toques simultáneos

## 💡 Consejos para Mejor Experiencia

1. **Modo Vertical**: El juego está optimizado para orientación vertical
2. **Pantalla Completa**: Desliza hacia abajo para ocultar la barra de navegación
3. **Multijugador**: Coloquen el dispositivo en una superficie plana
4. **Un Jugador**: La IA se vuelve más desafiante a medida que juegas

## 🔄 Actualizar el Proyecto

Si hay cambios en el código:

1. En Android Studio, ve a `Build` > `Clean Project`
2. Luego `Build` > `Rebuild Project`
3. Ejecuta nuevamente la aplicación

## 📞 Soporte

Si encuentras algún problema:
1. Verifica que cumples con los requisitos mínimos
2. Revisa la sección de solución de problemas
3. Verifica los logs en Android Studio (Logcat)

---

**¡Disfruta del juego! 🏒**

