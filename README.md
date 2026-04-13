# 🐾 BOOP — App de gestión de mascotas

Aplicación Android para la gestión integral de mascotas: perfiles, historial médico, recordatorios y alertas.

---

## 🚀 Tecnologías

- **Android** (Java, minSdk 26)
- **Firebase** — Auth, Firestore, Storage, Messaging
- **Google Maps** — Localización y mapas
- **Material Design 3**
- **Navigation Component**

---

## ⚙️ Configuración del proyecto

Para compilar el proyecto necesitas dos archivos locales que **no están en el repositorio** por seguridad:

### 1. `local.properties`

Crea este archivo en la **raíz del proyecto** (al lado de `build.gradle.kts`) con el siguiente contenido:

```properties
sdk.dir=/ruta/a/tu/Android/sdk
MAPS_API_KEY=TU_GOOGLE_MAPS_API_KEY
```

> Puedes obtener una clave de Google Maps en [Google Cloud Console](https://console.cloud.google.com) → APIs y servicios → Credenciales.

### 2. `app/google-services.json`

Descarga este archivo desde tu proyecto en [Firebase Console](https://console.firebase.google.com):

1. Abre tu proyecto Firebase
2. Ve a ⚙️ Configuración del proyecto
3. En la sección "Tus apps", descarga el `google-services.json`
4. Colócalo en la carpeta `app/`

> Puedes usar `app/google-services.json.example` como referencia de la estructura esperada.

---

## 🏗️ Compilar y ejecutar

1. Clona el repositorio:
   ```bash
   git clone https://github.com/TU_USUARIO/BOOP.git
   ```

2. Crea `local.properties` y añade `app/google-services.json` como se indica arriba.

3. Abre el proyecto en **Android Studio** y pulsa ▶️ Run.

---

## 📁 Estructura del proyecto

```
app/
├── src/main/
│   ├── java/com/boop/app/
│   │   ├── MainActivity.java        # Splash screen
│   │   ├── HomeActivity.java        # Actividad principal con navegación
│   │   ├── HomeFragment.java        # Fragment de inicio
│   │   ├── data/model/
│   │   │   ├── Pet.java             # Modelo de mascota
│   │   │   ├── PetAlert.java        # Modelo de alertas/recordatorios
│   │   │   └── MedicalRecord.java   # Modelo de historial médico
│   │   └── utils/
│   │       └── ThemeManager.java    # Gestión de tema claro/oscuro
│   ├── res/                         # Layouts, drawables, fuentes...
│   └── AndroidManifest.xml
├── google-services.json.example     # Estructura de referencia (sin claves reales)
└── build.gradle.kts
```

---

## 🔒 Seguridad

Los siguientes archivos están excluidos del repositorio mediante `.gitignore`:

| Archivo | Motivo |
|---|---|
| `local.properties` | Contiene la clave de Google Maps y la ruta al SDK |
| `app/google-services.json` | Contiene las claves de Firebase |
| `*.keystore` / `*.jks` | Certificados de firma de la app |

---

## 📄 Licencia

Este proyecto es de uso académico/personal.
