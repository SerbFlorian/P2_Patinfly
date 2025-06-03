# Práctica 2 - Patinfly

## Descripción general

Patinfly es una aplicación Android desarrollada con Jetpack Compose para alquilar bicicletas eléctricas. Muestra una lista de bicicletas disponibles con detalles como distancia, batería y tipo, y permite gestionar el perfil del usuario. En esta Práctica 2, se integra con una API REST para cargar datos, sincroniza con una base de datos local (Room) y actualiza los cambios en la base de datos local, garantizando acceso offline y consistencia.

## Contexto de esta versión

En la Práctica 1, los datos se cargaban desde ficheros JSON locales (bikes.json, user.json) y se almacenaban en una base de datos local. En esta Práctica 2, se implementan las siguientes mejoras:

- Integración con API REST: Reemplaza los ficheros JSON para obtener datos de bicicletas y usuarios desde una API remota.

## Estructura del proyecto
- `presentation`:
  - `MainActivity`: Pantalla principal con barra superior, perfil, lista de bicicletas y categorías.
  - `BikeListActivity`, BikeCard: Lista y tarjetas individuales de bicicletas.
  - `DetailBikeActivity`: Muestra detalles de una bicicleta específica.
  - `DetailRentBikeActivity`: Gestiona el alquiler mediante código QR.
  - `ProfileActivity`: Permite visualizar y editar el perfil del usuario.

- `data.repository`:
  - `BikeRepository`: Gestiona datos de bicicletas entre la API y Room.
  - `UserRepository`: Gestiona datos del usuario con sincronización.

- `domain.models`:
  - `Bike`: Modelo con atributos como UUID, distancia, batería y tipo.
  - `User`: Modelo con datos del usuario.
  - `ServerStatus`: Modelo para el estado del servidor.

## Cómo ejecutar la aplicación
- Clona este repositorio.
- Configura la URL de la API en RetrofitBike.(posiblemente, esto deje de funcionar, ya que cada año se hace mejoras en el enunciado del proyecto, esta es la práctica del 2025)
- Abre el proyecto en Android Studio.
- Compila y ejecuta en un emulador o dispositivo Android.
- Asegúrate de tener conexión a internet para la sincronización inicial; la app usa datos locales en modo offline.
