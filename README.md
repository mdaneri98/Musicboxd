# Musicboxd

> **Nota:** Este proyecto se encuentra actualmente en proceso de migración hacia arquitectura hexagonal. La refactorización se está realizando de manera incremental por entidades.

Musicboxd es una plataforma web para descubrir, reseñar y compartir opiniones sobre música. Los usuarios pueden explorar artistas, álbumes y canciones, escribir reseñas detalladas, calificar contenido musical y seguir a otros usuarios con gustos similares.

## Características principales

- **Gestión de contenido musical**: Catálogo completo de artistas, álbumes y canciones
- **Sistema de reseñas**: Reseñas de tipo álbum, artista y canción con ratings del 1 al 5
- **Interacción social**: Sistema de seguimiento de usuarios, comentarios en reseñas
- **Autenticación segura**: JWT-based authentication con Spring Security
- **Notificaciones**: Sistema de notificaciones para mantener a los usuarios informados
- **Búsqueda y filtrado**: Exploración avanzada del catálogo musical

## Stack tecnológico

### Backend
- **Java 21**
- **Spring Framework 5.3.33** - Dependency injection y gestión de transacciones
- **Jersey 2.43** - JAX-RS implementation para REST API
- **Hibernate 5.6.15** - ORM y JPA
- **PostgreSQL** - Base de datos relacional
- **Spring Security 4.2** - Autenticación y autorización
- **JWT (jjwt 0.11.2)** - Manejo de tokens de autenticación
- **Thymeleaf** - Templates para emails

### Testing
- JUnit 4
- Mockito 2
- HSQLDB (base de datos en memoria para tests)

## Estructura del proyecto

El proyecto sigue una arquitectura multi-módulo Maven:

```
paw-2024b-02/
├── models/          # Entidades de dominio y value objects
├── interfaces/      # Contratos de repositorios y servicios
├── persistence/     # Implementaciones de repositorios con JPA
├── services/        # Casos de uso y lógica de aplicación
├── webapp/          # Controllers REST, DTOs y configuración web
└── frontend/        # Aplicación cliente (React/SPA)
```

## Requisitos previos

### Con Docker
- Docker 20.10+
- Docker Compose 2.0+

### Sin Docker
- Java 21+
- Maven 3.6+
- PostgreSQL 12+
- Node.js 20.11.0+

## Instalación y ejecución

### Con Docker

```bash
docker-compose up --build
```

**URLs de acceso:**
- Aplicación: http://localhost:8080
- API: http://localhost:8080/api
- Base de datos: localhost:5432 (user: `musicboxd`, pass: `musicboxd`)

**Comandos útiles:**
```bash
# Modo background
docker-compose up -d

# Ver logs
docker-compose logs -f backend

# Acceder a PostgreSQL
docker-compose exec postgres psql -U musicboxd -d postgres

# Resetear base de datos
docker-compose down -v

# Hot-reload frontend (desarrollo)
docker-compose --profile dev up

# Producción
docker-compose -f docker-compose.prod.yml up --build
```

**Variables de entorno opcionales** (`.env`):
```bash
DATABASE_USERNAME=musicboxd
DATABASE_PASSWORD=your-password
JWT_SECRET=your-secret
MAILER_EMAIL=your-email@gmail.com
MAILER_PASSWORD=your-app-password
```

### Instalación manual (sin Docker)

Si prefieres no usar Docker o necesitas desarrollar directamente con Maven:

#### 1. Base de datos

```bash
# Crear base de datos PostgreSQL
createdb musicboxd

# Ejecutar schema (desde la raíz del proyecto)
psql -U postgres -d musicboxd -f persistence/src/main/resources/schemas.sql

# (Opcional) Cargar datos de prueba
psql -U postgres -d musicboxd -f persistence/src/main/resources/queries.sql
```

#### 2. Configurar credenciales

Editar `webapp/src/main/resources/application.properties`:

```properties
database.url=jdbc:postgresql://localhost:5432/musicboxd
database.username=tu-usuario
database.password=tu-password
```

#### 3. Compilar e instalar

```bash
mvn clean install
```

**Nota:** Este comando también construye el frontend automáticamente via frontend-maven-plugin.

#### 4. Ejecutar backend

```bash
cd webapp
mvn jetty:run
```

La API estará disponible en `http://localhost:8080/paw-2024b-02`

#### 5. Ejecutar tests

```bash
mvn test
```

---

**Compatibilidad:** Docker y Maven pueden usarse simultáneamente sin conflictos. Los cambios en `application.properties` afectan solo a la ejecución local, mientras que Docker usa variables de entorno.

## Estado de Arquitectura Hexagonal

El proyecto se está migrando desde una arquitectura en capas tradicional hacia arquitectura hexagonal (puertos y adaptadores) para mejorar la separación de responsabilidades y la testabilidad.

### Entidades migradas ✓
- **User** - Gestión de usuarios y autenticación
- **Artist** - Artistas musicales
- **Album** - Álbumes musicales
- **Song** - Canciones
- **Review** - Sistema de reseñas (AlbumReview, ArtistReview, SongReview)

### Entidades pendientes
- Comment
- Notification
- RefreshToken
- UserVerification

### Estructura hexagonal

```
Domain Layer (models/domain/)
├── Entidades ricas con lógica de negocio
├── Value Objects (Rating, ArtistId, UserId, etc.)
└── Repository Ports (interfaces)

Application Layer (services/usecases/)
├── Casos de uso (CreateUser, UpdateAlbum, etc.)
└── Domain services

Infrastructure Layer (persistence/)
├── JPA Entities (solo para persistencia)
├── Repository Adapters (implementaciones JPA)
└── Mappers (Domain ↔ JPA)

Presentation Layer (webapp/)
└── REST Controllers
```

## API REST

La API sigue el estilo REST con hypermedia (HATEOAS). Los endpoints principales incluyen:

- `/api/users` - Gestión de usuarios
- `/api/artists` - Artistas musicales
- `/api/albums` - Álbumes
- `/api/songs` - Canciones
- `/api/reviews` - Reseñas
- `/api/comments` - Comentarios
- `/api/auth` - Autenticación y tokens

Documentación completa disponible en `/api` (en desarrollo).

## Contribución

Este proyecto es parte de un trabajo académico de ITBA. Para contribuir:

1. Crear un branch desde `master`
2. Realizar los cambios siguiendo las convenciones del proyecto
3. Asegurar que todos los tests pasen
4. Crear un Pull Request con descripción clara

## Licencia

Proyecto académico - ITBA PAW 2024B
