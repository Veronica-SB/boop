# 🐾 Boop

**Toda la información de tu mascota, en un solo sitio.**

Boop es una app Android nativa para propietarios de mascotas. Reúne en un mismo lugar el historial veterinario, los recordatorios de cuidados y la coordinación entre todas las personas que cuidan del animal. Además, conecta al usuario con otros propietarios y con servicios cercanos.

> Proyecto final del CFGS en Desarrollo de Aplicaciones Multiplataforma (curso 2025/26). Estado: **prototipo funcional**.

▶️ **[Ver vídeo de presentación] **
https://youtu.be/KMsVTUCCjsg


---

## El problema

Gran parte de la información sobre la salud de una mascota sigue estando en papel, como la cartilla de vacunas o los informes veterinarios. Lo demás acaba repartido entre notas del móvil y mensajes de WhatsApp. Cuando varias personas cuidan del mismo animal, coordinarse sin una herramienta común provoca olvidos y errores, como vacunas sin renovar o dosis de medicación repetidas.

## Funcionalidades

- **Historial médico digital.** Ficha de la mascota (especie, raza, peso, chip, foto) con el registro de vacunas, medicación, visitas veterinarias, alergias e intolerancias.
- **Alertas y recordatorios.** Avisos de vacunas, desparasitación, medicación y citas, con la antelación que elija el usuario.
- **Gestión colaborativa.** El propietario invita a otros cuidadores por correo. Todos ven el mismo perfil, actualizado en tiempo real, y el propietario puede retirar el acceso cuando quiera.
- **Comunidad.** Publicaciones por categorías (preguntas, adopción, intercambio), con comentarios y "me gusta".
- **Mapa de servicios.** Clínicas veterinarias, peluquerías, tiendas y espacios pet friendly cerca del usuario, con filtros por tipo.
- **Cuenta y privacidad.** Consentimiento granular en el registro, gestión de notificaciones, modo claro u oscuro y eliminación de la cuenta con todos sus datos, conforme al RGPD.

## Tecnologías

| Área | Tecnología |
|---|---|
| Lenguaje y entorno | Java (JDK 21) · Android Studio |
| Autenticación | Firebase Authentication (correo y contraseña) |
| Base de datos | Cloud Firestore, con sincronización en tiempo real mediante listeners |
| Notificaciones | WorkManager + notificaciones locales |
| Mapas | OpenStreetMap (OSMDroid) + Overpass API |
| Diseño | Figma · Adobe Illustrator y Photoshop |
| Organización | Git y GitHub · Trello · Scrum con sprints de dos semanas |

## Decisiones técnicas

**De Google Maps a OpenStreetMap.** Al principio el mapa iba a usar Google Maps y Places API. Para activarlas, Google Cloud exige vincular una tarjeta personal y, pasado el periodo de prueba, puede haber costes elevados. Optamos por OSMDroid con Overpass API para buscar servicios por categoría. Es gratuito, no necesita API key y se integra con una sola dependencia. A cambio, los mapas son más sencillos y las consultas grandes van más lentas.

**Notificaciones sin servidor.** En lugar de enviar las notificaciones desde un backend con Firebase Cloud Messaging, una tarea periódica de WorkManager lee los recordatorios en Firestore desde el propio dispositivo y lanza una notificación local cuando alguno está a punto de vencer. Es una solución sencilla y adecuada para el prototipo, aunque tiene una limitación: si el usuario fuerza el cierre de la app o la desinstala, deja de recibir avisos.

## Cómo ejecutarlo

**Requisitos:** Android Studio, JDK 21 y un proyecto de Firebase propio.

1. Clona el repositorio:
   ```bash
   git clone https://github.com/Veronica-SB/boop.git
   ```
2. En la [consola de Firebase](https://console.firebase.google.com/), crea un proyecto, añade una app Android con el mismo nombre de paquete que el proyecto y descarga `google-services.json`.
3. Copia `google-services.json` en la carpeta `app/`.
4. En Firebase, activa **Authentication** (proveedor correo/contraseña) y **Cloud Firestore**.
5. Abre el proyecto en Android Studio, sincroniza Gradle y ejecútalo en un emulador o dispositivo.

## Estado y próximos pasos

Boop es un **prototipo funcional**: los módulos principales están implementados, pero la app no está publicada ni pensada todavía para producción. Mejoras previstas:

- Enviar las notificaciones desde un servidor, para que lleguen aunque la app esté cerrada.
- Pulir la interfaz y la experiencia de uso.
- Añadir pruebas automatizadas.

## Equipo

Boop nació como un proyecto personal de **Verónica Silva Barbosa**, que propuso al equipo continuarlo juntos como proyecto final de DAM.

| | Rol |
|---|---|
| **Verónica Silva Barbosa** · [GitHub](https://github.com/Veronica-SB) | Idea original y primera versión del proyecto, estudio de usuarios y necesidades, capa de datos con Firebase, desarrollo en Java y control de versiones con Git |
| **Brenda Barba Montoro** | Coordinación, backend y QA |
| **Narcís Garcia Torroella** | Frontend, diseño UX/UI e identidad gráfica |
| **María Santolaya Fernández** | Investigación, base de datos y QA |
