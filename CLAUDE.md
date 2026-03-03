# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Musicboxd** is a music review and social platform similar to Letterboxd but for music. It's a full-stack web application built with:
- **Backend**: Spring Framework 5.3.33, Jersey 2.43 REST API, Hibernate JPA 5.6.15
- **Frontend**: Next.js 16 (Pages Router), React 19, TypeScript, Redux Toolkit
- **Database**: PostgreSQL
- **Build Tool**: Maven (multi-module project)
- **Security**: JWT-based authentication with Spring Security

## Build & Run Commands

### Full Application Build

```bash
# Build entire project (backend + frontend)
mvn clean package

# Build without running tests (tests are currently skipped by default)
mvn clean package -DskipTests=true

# Install dependencies and build all modules
mvn clean install
```

### Backend Development

```bash
# Run the webapp module (requires PostgreSQL running)
cd webapp
mvn jetty:run

# The backend API will be available at http://localhost:8080/api
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Run development server (with hot reload)
npm run dev
# Opens at http://localhost:3000

# Build production bundle
npm run build

# Preview production build
npm run start

# Run tests
npm test
npm run test:watch

# Linting and formatting
npm run lint
npm run format
npm run format:check
```

### Database Setup

The application requires PostgreSQL. Default configuration in `webapp/src/main/resources/application.properties`:
- Database: `paw-2024b-02`
- Username: `paw-2024b-02`
- Password: (see application.properties)
- URL: `jdbc:postgresql://localhost:5432/paw-2024b-02`

Database schema is automatically initialized via `schemas.sql` and updated by Hibernate (`hibernate.hbm2ddl.auto=update`).

### Running Tests

```bash
# Backend tests (JUnit + Mockito)
cd services  # or persistence
mvn test

# Frontend tests (Jest + React Testing Library)
cd frontend
npm test
npm run test:coverage
```

## Architecture

### Multi-Module Maven Structure

```
paw-2024b-02/
├── models/          - JPA entities (Album, Artist, Song, User, Review, etc.)
├── interfaces/      - Service and DAO interfaces (contracts)
├── persistence/     - DAO implementations (JPA repositories)
├── services/        - Business logic implementations
├── webapp/          - REST API controllers, DTOs, config, security
└── frontend/        - Next.js SPA
```

### Backend Layering

**models** → **interfaces** ← **persistence** → **services** → **webapp**

1. **Models**: JPA entities with Hibernate annotations
   - Domain objects: `Album`, `Artist`, `Song`, `User`, `Review`, `Comment`, etc.
   - Located in package: `ar.edu.itba.paw.models`

2. **Interfaces**: Service and DAO contracts
   - Service interfaces: `AlbumService`, `UserService`, etc.
   - DAO interfaces: `AlbumDao`, `UserDao`, etc.
   - Located in: `ar.edu.itba.paw.services` and `ar.edu.itba.paw.persistence`

3. **Persistence**: JPA DAO implementations
   - Classes: `AlbumJpaDao`, `UserJpaDao`, etc.
   - Use `EntityManager` for database operations
   - Transaction management via Spring `@Transactional`

4. **Services**: Business logic layer
   - Classes: `AlbumServiceImpl`, `UserServiceImpl`, etc.
   - Orchestrate multiple DAOs, handle business rules
   - Annotated with `@Service`

5. **Webapp**: REST API layer
   - **Controllers**: Jersey REST endpoints (`@Path`, `@GET`, `@POST`, etc.)
   - **DTOs**: Data Transfer Objects for API requests/responses
   - **Mappers**: Convert between models and DTOs
   - **Auth**: JWT authentication and Spring Security filters
   - **Config**: Spring configuration classes

### Frontend Architecture

```
frontend/
├── pages/            - Next.js pages (routing)
├── components/       - React components (layout, cards, forms, ui)
├── store/            - Redux store and slices
├── repositories/     - API repositories (HATEOAS-aware)
├── lib/              - Core libraries (apiClient with Axios)
├── types/            - TypeScript type definitions
├── hooks/            - Custom React hooks
└── utils/            - Utility functions
```

**Key Frontend Concepts:**
- **Repository Pattern**: Each repository (e.g., `AlbumRepository`, `UserRepository`) encapsulates API calls for a specific resource
- **HATEOAS**: The API client in `lib/apiClient.ts` handles hypermedia links and pagination via RFC 5988 Link headers
- **JWT Auth**: Stored in localStorage; automatic token refresh on 401/403 responses
- **State Management**: Redux Toolkit with typed hooks (`store/hooks.ts`)

## API Design

### HATEOAS REST API

The backend follows HATEOAS (Hypermedia as the Engine of Application State):
- Resources include `_links` with relations (self, next, prev, etc.)
- Collection responses use RFC 5988 Link headers for pagination
- The frontend `apiClient.getCollection()` automatically parses Link headers into pagination metadata

### Authentication Flow

1. **Login**: `POST /api/auth/login` → Returns JWT access token + refresh token in headers
2. **Token Storage**: Frontend stores tokens in localStorage
3. **Authorized Requests**: Include `Authorization: Bearer {token}` header
4. **Token Refresh**: On 401/403, automatically retry with refresh token
5. **Logout**: Clear tokens from localStorage

JWT configuration is in `application.properties`:
- `jwt.access.expiration`: 480000ms (8 minutes)
- `jwt.refresh.expiration`: 86400000ms (24 hours)

## Development Workflow

### Making Changes to Backend

1. Modify the appropriate layer (model, DAO, service, or controller)
2. Update interfaces if adding new methods
3. Rebuild the affected module: `mvn clean install` in module directory
4. Restart jetty: `mvn jetty:run` in webapp directory
5. Test via frontend or API client (e.g., Postman, curl)

### Making Changes to Frontend

1. Modify components, pages, or repositories
2. Changes auto-reload via Next.js dev server (`npm run dev`)
3. Ensure types are correct (TypeScript will show errors)
4. Test in browser and run unit tests if applicable

### Common Development Tasks

**Add a new entity:**
1. Create model in `models/`
2. Create DAO interface in `interfaces/persistence/`
3. Implement DAO in `persistence/` (extend `CrudDao` if applicable)
4. Create service interface in `interfaces/services/`
5. Implement service in `services/`
6. Create DTOs and controller in `webapp/`
7. Add frontend types, repository, and UI components

**Add a new API endpoint:**
1. Add method to service interface and implementation
2. Create controller method in `webapp/controller/` with Jersey annotations
3. Create/update DTOs in `webapp/dto/`
4. Add mapper if needed in `webapp/mapper/`
5. Update frontend repository to call new endpoint
6. Update frontend UI to use the new functionality

## Testing Strategy

### Backend Tests
- **Unit Tests**: Service layer tests with Mockito (services/src/test/java)
- **Integration Tests**: DAO layer tests with HSQLDB in-memory database (persistence/src/test/java)
- Tests use JUnit 4
- Spring Test context for integration tests

### Frontend Tests
- **Unit Tests**: Component tests with Jest + React Testing Library
- **Mocking**: Nock for HTTP mocking, Sinon for function stubs
- Located in component directories with `.test.ts` or `.test.tsx` extension

## Configuration Files

### Backend Configuration
- `webapp/src/main/resources/application.properties` - Main application config (database, JWT, email)
- `webapp/src/main/webapp/WEB-INF/web.xml` - Servlet configuration
- `webapp/src/main/java/ar/edu/itba/paw/webapp/config/` - Spring Java-based config

### Frontend Configuration
- `frontend/.env.development` - Development environment variables
- `frontend/.env.production` - Production environment variables
- Key variable: `NEXT_PUBLIC_API_BASE_URL` (default: http://localhost:8080/api)

## Key Packages and Dependencies

### Backend
- **Spring**: Core DI, MVC, ORM, TX, Security
- **Jersey**: JAX-RS implementation for REST API
- **Hibernate**: JPA implementation for ORM
- **Jackson**: JSON serialization/deserialization
- **JJWT**: JWT token generation and validation
- **Thymeleaf**: Email template engine
- **Logback + SLF4J**: Logging

### Frontend
- **Next.js**: React framework with SSG/SSR
- **Redux Toolkit**: State management with simplified API
- **Axios**: HTTP client with interceptors
- **React Hook Form + Yup**: Form validation
- **i18next**: Internationalization (i18n)
- **JWT-decode**: Decode JWT tokens client-side

## Important Notes

- **Tests are skipped by default**: `<skipTests>true</skipTests>` in root pom.xml
- **Base path in production**: Frontend uses `/paw-2024b-02` base path in production
- **Hibernate auto-update**: Schema changes are automatically applied on startup
- **CORS**: Configured in Spring Security for frontend development (localhost:3000)
- **Maven builds frontend**: The frontend module uses `frontend-maven-plugin` to run npm commands during Maven build
