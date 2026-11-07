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
| 5 | `AppContainer` + `Application` + Manifest | ⏳ |
| 6 | UI: Login, Registro y CRUD con Navigation | ⏳ |

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

Errores de dominio (`domain/error/AppException`): `Unauthorized` (401), `NotFound` (404), `Conflict` (409), `InvalidData` (400/422), `Network` (sin conexión), `Unknown`. Si `/games` responde 401, la sesión se borra para volver al Login.

## Backend
La API vive en un proyecto separado, **GameJamAPI** (FastAPI). Desde el emulador se consume en `http://10.0.2.2:8000/`. Cada usuario sólo ve sus propios juegos; las rutas `/games` requieren `Authorization: Bearer <token>`.

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
