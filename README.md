# GameJam · Bitácora de videojuegos

App Android (Proyecto Unidad 3 · Desarrollo de aplicaciones móviles) con **registro, inicio de sesión y CRUD** del recurso principal: **Game** (juego).

## Stack
- Kotlin · Jetpack Compose + Material 3
- MVVM (ViewModel + StateFlow) · Navigation Compose
- Retrofit 3 + kotlinx.serialization + OkHttp (interceptor `Authorization: Bearer`)
- Sesión en SharedPreferences detrás de una abstracción
- Inyección de dependencias manual (`AppContainer`)
- Dependencias en Version Catalog (`gradle/libs.versions.toml`)

## Avance
| Paso | Descripción | Estado |
|---|---|---|
| 0 | Repositorio Git | ✅ |
| 1 | Dependencias (red, navegación, serialización, lifecycle-compose) | ✅ |
| 2 | Capa `domain`: modelo `Game` e interfaces de repositorio | ✅ |
| 3 | Backend: API FastAPI en proyecto aparte (`GameJamAPI`) | ✅ |
| 4 | Capa `data`: DTOs, Mappers, Api, sesión, interceptor, repositorios | ✅ |
| 5 | `AppContainer` + `Application` + Manifest | ✅ |
| 6a | UI: navegación, Login, Registro y cierre de sesión | ✅ |
| 6b | UI: lista de juegos (Home) con carga, vacío y error | ✅ |
| 6c | UI: detalle y eliminar con confirmación | ✅ |
| 6d | UI: formulario de crear y editar | ✅ |

## Dominio
**Game** (recurso principal: juego de la bitácora)

| Atributo | Tipo | Validación |
|---|---|---|
| id | Int | Lo asigna la API |
| title | String | Obligatorio, 1–60 caracteres |
| platform | String | Obligatoria |
| hoursPlayed | Int | ≥ 0 |
| rating | Int | 1–10 |
| startDate | LocalDate | No puede ser futura |
| completed | Boolean | — |

Contratos en `domain/repository`: `AuthRepository` (registro, login, sesión) y `GameRepository` (CRUD). Ambos devuelven `Result<T>` con modelos de dominio (`Game`, `User`).

`minSdk = 26` para usar `java.time.LocalDate`.

## Convenciones
- Código y nombres de archivo en inglés, descriptivos.
- Comentarios breves, sin acentos ni caracteres especiales.

## Capa data
| Paquete | Contenido |
|---|---|
| `data/remote/dto` | DTOs `@Serializable` (`AuthDtos`, `GameDtos`); nunca salen de `data` |
| `data/remote/api` | Interfaces Retrofit `AuthApiService` y `GameApiService` |
| `data/remote/interceptor` | `AuthInterceptor`: agrega `Authorization: Bearer <token>` |
| `data/remote/SafeApiCall.kt` | Convierte errores HTTP y de red en `AppException` |
| `data/local` | `SessionStorage` (abstracción) y `SharedPreferencesSessionStorage` |
| `data/mapper` | Mappers DTO ↔ dominio (`GameMapper`, `UserMapper`) |
| `data/repository` | `AuthRepositoryImpl` y `GameRepositoryImpl` |

Errores de dominio (`domain/error/AppException`): `Unauthorized` (401), `NotFound` (404), `Conflict` (409), `InvalidData` (400/422), `Network` (sin conexión), `Unknown` (cualquier otro error, por ejemplo una fecha con formato inesperado; la app nunca se cierra por eso). Si `/games` responde 401, la sesión se borra para volver al Login.

## Inyección de dependencias
- `di/AppContainer`: interfaz con `authRepository` y `gameRepository`.
- `di/DefaultAppContainer`: construye **una sola vez** `SharedPreferencesSessionStorage`, `OkHttpClient` (con `AuthInterceptor` y logging sólo en debug), `Retrofit` y los repositorios.
- `GameJamApplication`: crea el contenedor en `onCreate()`; está registrada en el Manifest.

## Red
- URL base en `BuildConfig.API_BASE_URL` (`app/build.gradle.kts`): `http://10.0.2.2:8000/`, el alias del emulador para la PC.
- Cuando la API se despliegue en la nube, cambiar esa URL por la pública (con HTTPS no hace falta tocar la configuración de red).
- Si el emulador no llega a `10.0.2.2`, alternativa local: ejecutar en la pestaña **Terminal** de Android Studio (Alt+F12)
  ```powershell
  & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:8000 tcp:8000
  ```
  y usar temporalmente `http://localhost:8000/` como URL base. Hay que repetir el comando cada vez que se enciende el emulador.
- HTTP en texto plano permitido **sólo en debug** para `10.0.2.2` y `localhost` (`app/src/debug/res/xml/network_security_config.xml`).
- Para un teléfono físico: usar la IP de la PC en la red local y agregarla en ese mismo archivo.

## Interfaz
| Paquete | Contenido |
|---|---|
| `ui/navigation` | Rutas tipadas (`AppRoutes`) y `AppNavHost` |
| `ui/auth` | Login y Registro (Screen + ViewModel + UiState) y reglas de validación (`AuthValidation`) |
| `ui/games/detail` | Detalle con todos los atributos (`DetailRow`), botón Editar, eliminar con `DeleteGameDialog` y Snackbar ante errores |
| `ui/games/form` | Formulario reutilizable para crear y editar (`GameFormScreen`), reglas en `GameFormValidation` y `StartDatePickerDialog` que bloquea fechas futuras |
| `ui/games/list` | Home: `LazyColumn` de juegos (`GameListItem`), estados de carga, vacío y error con reintento, botón "Agregar juego" y cierre de sesión |
| `ui/components` | `FormTextField`, `PasswordTextField`, `LoadingButton`, `ErrorText`, `FullScreenLoading`, `FullScreenMessage`, `FullScreenError`, `UserMessageEffect` (Snackbar de un solo uso) |
| `ui/common` | `appContainer()` para las factories, `toMessageRes()` para mensajes de error y `toDisplayText()` para fechas ("15 de octubre de 2026") |

- Cada pantalla tiene un `XScreen` (observa el `StateFlow` con `collectAsStateWithLifecycle`) y un `XContent` sin estado.
- Los ViewModels reciben el repositorio por constructor y se crean con `viewModelFactory { initializer { … } }`.
- Al iniciar sesión, registrarse o cerrar sesión se limpia la pila de navegación, así que Atrás no regresa.
- Si hay sesión guardada, la app abre directo en Home.
- Home recarga la lista cada vez que vuelve a mostrarse (`ON_RESUME`), así se actualiza después de crear, editar o eliminar.
- Si la API responde 401, la sesión se borra y la app vuelve al Login mostrando "Tu sesión expiró, inicia sesión de nuevo" (`LoginRoute(sessionExpired = true)`).
- Los clics que navegan sólo se atienden si la pantalla está activa (`whenResumed`), así un doble toque no abre dos pantallas ni vacía la pila.
- Cada mensaje de Snackbar se marca como mostrado antes de enseñarse, así no se repite al volver a una pantalla.
- Retroalimentación (RF-10): al crear, editar o eliminar, la pantalla deja un mensaje en el `savedStateHandle` de la anterior (`KEY_USER_MESSAGE`) y ésta lo muestra en un Snackbar ("Juego agregado", "Cambios guardados", "Juego eliminado"). Los errores de recarga, guardado o eliminación también salen en Snackbar.
- El formulario usa `GameFormRoute(gameId)`: `NEW_GAME_ID` (0) crea un juego nuevo; cualquier otro id lo edita con los datos precargados.
- Textos de la interfaz en `res/values/strings.xml`.
- Paleta oscura inspirada en el cliente de Steam (`ui/theme/Color.kt`); sin modo claro ni color dinámico.

## Backend
La API vive en un proyecto separado, **GameJamAPI** (FastAPI). Desde el emulador se consume en `http://10.0.2.2:8000/` (ver sección Red). Cada usuario sólo ve sus propios juegos; las rutas `/games` requieren `Authorization: Bearer <token>`.

## Dependencias agregadas
| Librería | Versión | Uso |
|---|---|---|
| navigation-compose | 2.9.0 | Navegación entre pantallas |
| lifecycle-viewmodel-compose / runtime-compose | 2.11.0 | `viewModel()` y `collectAsStateWithLifecycle()` |
| retrofit + converter-kotlinx-serialization | 3.0.0 | Cliente HTTP |
| okhttp logging-interceptor | 4.12.0 | Log de peticiones en depuración |
| kotlinx-serialization-json | 1.9.0 | DTOs `@Serializable` |

## Cómo compilar
1. Abrir en Android Studio y hacer **Sync Now**.
2. **Build → Assemble Project** (Ctrl+F9).
