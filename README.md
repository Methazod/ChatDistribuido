# ChatDistribuido

Este proyecto implementa un sistema de chat distribuido utilizando el lenguaje de programación Java. Está diseñado para permitir la comunicación en tiempo real entre múltiples clientes a través de un servidor central.

## Características

- **Cliente (`Cliente.java`)**: Representa a los usuarios que se conectan al chat, permitiendo el envío y recepción de mensajes.
- **Servidor (`Servidor.java`)**: Gestiona las conexiones de los clientes y distribuye los mensajes a todos los participantes en las salas de chat.
- **Salas de Chat (`Sala.java`)**: Facilita la organización de conversaciones en diferentes grupos o temas.
- **Conexiones (`Conexion.java`)**: Maneja la comunicación entre clientes y servidor, asegurando una transmisión de datos eficiente.
- **Hilos de Cliente (`ThreadClienteEscribir.java` y `ThreadClienteLeer.java`)**: Gestionan la entrada y salida de datos en el lado del cliente, permitiendo la interacción en tiempo real.
- **Hilos de Servidor (`ThreadServidor.java`)**: Maneja las operaciones concurrentes en el servidor, permitiendo la gestión de múltiples conexiones simultáneamente.
- **Utilidades (`Utilities.java`)**: Proporciona funciones auxiliares para operaciones comunes dentro del sistema.

## Estructura del Proyecto

El proyecto contiene los siguientes archivos principales:

- `Cliente.java`: Define la estructura y funcionalidades del cliente de chat.
- `Servidor.java`: Implementa la lógica del servidor que coordina la comunicación entre clientes.
- `Sala.java`: Gestiona las diferentes salas de chat disponibles.
- `Conexion.java`: Establece y mantiene las conexiones de red entre clientes y servidor.
- `MainCliente.java`: Clase principal para ejecutar la aplicación cliente.
- `MainServidor.java`: Clase principal para iniciar el servidor de chat.
- `ThreadClienteEscribir.java`: Maneja la captura y envío de mensajes desde el cliente.
- `ThreadClienteLeer.java`: Gestiona la recepción y visualización de mensajes en el cliente.
- `ThreadServidor.java`: Administra las conexiones y comunicaciones en el lado del servidor.
- `Utilities.java`: Incluye funciones de apoyo para diversas operaciones en el sistema.

## Requisitos Previos

- **Java Development Kit (JDK)**: Asegúrate de tener instalado JDK 8 o superior en tu sistema. Puedes descargarlo desde [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) o [OpenJDK](https://openjdk.java.net/install/).

## Cómo Ejecutar el Proyecto

1. **Clonar el repositorio**:
   git clone https://github.com/Methazod/ChatDistribuido.git
   cd ChatDistribuido
2. **Iniciar el servidor**.
3. **Iniciar los clientes**.

## Funcionamiento
-  Servidor: Actúa como intermediario, recibiendo mensajes de los clientes y distribuyéndolos a todos los participantes en la misma sala de chat.
-  Clientes: Se conectan al servidor, permiten a los usuarios enviar mensajes y recibir los mensajes de otros participantes en tiempo real.
Cada cliente puede unirse a diferentes salas de chat o crear nuevas, facilitando conversaciones organizadas por temas o grupos de interés.

## Contribuciones
Las contribuciones son bienvenidas. Si deseas mejorar este proyecto, por favor sigue estos pasos:

1. Haz un fork del repositorio.
2. Crea una nueva rama para tu función o corrección:
  git checkout -b nombre-de-tu-rama
3. Realiza tus cambios y haz commits descriptivos.
4. Envía un pull request detallando tus modificaciones.
