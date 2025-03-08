Voy a crear un prompt detallado que pueda servir como guía para otra sesión de chat de Cursor, explicando el proyecto NightClubCraft y sus componentes principales.

Ran tool

Ran tool

Ran tool

Ran tool

Ran tool

Ran tool

Read file: README.md

Read file: src/main/java/nightclubcraftmod/NightClubCraftMod.java
Basado en toda la información recopilada, aquí está el prompt detallado para guiar a otra sesión de chat de Cursor:

# Prompt para Guiar el Desarrollo del Mod NightClubCraft para Minecraft

## Descripción General del Proyecto

NightClubCraft es un mod para Minecraft 1.20.1 que transforma el juego en un simulador de gestión de clubes nocturnos y vida nocturna. El mod se encuentra actualmente en fase de desarrollo temprano (pre-alpha) y utiliza el framework Fabric.

El objetivo principal es permitir a los jugadores diseñar, construir y gestionar sus propios clubes nocturnos, atraer clientes, organizar eventos y convertirse en magnates de la vida nocturna en el universo de Minecraft.

## Estructura del Proyecto

El proyecto sigue una estructura típica de mod de Fabric:

```
NightClubCraftMod/
├── src/main/java/nightclubcraftmod/
│   ├── client/
│   │   ├── gui/
│   │   │   ├── screen/         # Pantallas personalizadas
│   │   │   │   ├── DevToolsScreen.java
│   │   │   │   ├── LoadGameScreen.java
│   │   │   │   ├── NewGameScreen.java
│   │   │   │   └── SlotSelectionScreen.java
│   │   │   └── widget/         # Widgets personalizados
│   │   │       └── CustomButtonWidget.java
│   │   └── ModMenuIntegration.java
│   ├── mixin/                  # Mixins para modificar el comportamiento del juego
│   ├── util/                   # Utilidades generales
│   ├── NightClubCraftMod.java  # Clase principal del mod
│   └── NightClubCraftModDataGenerator.java
├── resources/                  # Recursos del mod (texturas, modelos, etc.)
├── libs/                       # Bibliotecas externas (ModMenu)
└── build.gradle               # Configuración de Gradle
```

## Componentes Principales Implementados

1. **Interfaz Gráfica Personalizada**:
   - `CustomButtonWidget`: Botones con estilo personalizado para la interfaz del mod
   - Pantallas de nuevo juego y carga de partidas con slots personalizados
   - Sistema de scroll implementado en la pantalla de herramientas de desarrollo

2. **Integración con ModMenu**:
   - Permite acceder a las herramientas de desarrollo desde la interfaz de ModMenu
   - Facilita la gestión del mod durante el desarrollo

3. **Herramientas de Desarrollo**:
   - `DevToolsScreen`: Panel con herramientas para facilitar el desarrollo y pruebas
   - Creación rápida de mundos de prueba
   - Acceso a funcionalidades de prueba específicas del mod

## Tareas Pendientes y Posibles Mejoras

1. **Sistema de Economía y Gestión**:
   - Implementar sistema de moneda y transacciones
   - Desarrollar mecánicas de gestión de personal
   - Crear sistema de reputación y clientela

2. **Construcción y Decoración**:
   - Añadir bloques y elementos decorativos temáticos
   - Implementar sistema de iluminación dinámica
   - Crear herramientas de construcción específicas para clubes

3. **Eventos y Entretenimiento**:
   - Sistema de DJs y música personalizada
   - Mecánicas de eventos especiales
   - Interacción con NPCs y clientes

4. **Marcas y Patrocinios**:
   - Implementar sistema de acuerdos con marcas
   - Crear mecánicas de patrocinio y exclusividad
   - Desarrollar eventos promocionales

5. **Optimización y Pulido**:
   - Mejorar rendimiento en servidores
   - Refinar la interfaz de usuario
   - Añadir más opciones de personalización

## Guía para Contribuir al Proyecto

1. **Configuración del Entorno**:
   - Asegúrate de tener Java JDK 17 o superior
   - Clona el repositorio y configura Gradle
   - Ejecuta `./gradlew build` para compilar el mod

2. **Convenciones de Código**:
   - Sigue el estilo de código existente
   - Documenta todas las clases y métodos principales
   - Mantén la estructura de paquetes organizada

3. **Flujo de Trabajo**:
   - Trabaja en ramas separadas para cada característica
   - Realiza pruebas exhaustivas antes de integrar cambios
   - Utiliza las herramientas de desarrollo incluidas para probar

4. **Recursos y Referencias**:
   - Consulta la documentación de Fabric API
   - Revisa el README para entender la visión del proyecto
   - Utiliza las herramientas de desarrollo incluidas para facilitar las pruebas

## Objetivos Inmediatos

1. Implementar el sistema básico de economía y gestión
2. Añadir los primeros bloques y elementos decorativos específicos
3. Desarrollar la mecánica de clientes y reputación
4. Mejorar la interfaz de usuario con más elementos personalizados
5. Implementar el sistema de música y ambiente

Este prompt proporciona una visión general del proyecto NightClubCraft, su estructura, componentes principales y tareas pendientes. Puede servir como guía para cualquier desarrollador que quiera contribuir al proyecto o entender su funcionamiento.
