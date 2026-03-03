¡Excelente base! Tenés los conceptos clave muy bien identificados. El documento original ya cubría mucho terreno, pero le faltaba un poco de profundidad técnica en las explicaciones, ejemplos de código más completos y algunas correcciones semánticas (sobre todo en idempotencia y el ciclo de vida de JAX-RS).

Acá tenés la versión ampliada, corregida y formateada para que quede como una guía profesional de referencia.

---

# 📘 Estándares de JAX-RS y REST APIs (Guía Completa)

## 🌐 1️⃣ REST: Principios y Estándares

### 📌 ¿Qué es REST?

**REST (Representational State Transfer)** es un estilo arquitectónico para sistemas hipermedia distribuidos, definido por Roy Fielding en su tesis doctoral (año 2000).
No es un protocolo ni una especificación formal, sino un conjunto de **6 restricciones arquitectónicas**. Si un sistema cumple (la mayoría de) estas restricciones, se lo considera **RESTful**.

### 🧱 1.1 Las 6 Restricciones de REST

1. **Client-Server (Cliente-Servidor):**
* Separación de responsabilidades. El cliente se encarga de la UI/UX y el servidor del almacenamiento y procesamiento de datos.
* **Beneficio:** Permite que ambos evolucionen de forma independiente y mejora la portabilidad del cliente.


2. **Stateless (Sin estado):**
* Cada request del cliente al servidor debe contener **toda** la información necesaria para entender y procesar la petición.
* ❌ **Incorrecto:** Guardar sesiones en memoria del servidor (ej. `HttpSession`).
* ✅ **Correcto:** Enviar un token (ej. JWT) en el header `Authorization` de cada request.
* **Beneficio:** Escalabilidad horizontal extrema.


3. **Cacheable (Almacenable en caché):**
* Las respuestas deben definirse explícitamente como cacheables o no cacheables.
* Se gestiona mediante headers HTTP (`Cache-Control`, `ETag`, `Last-Modified`).
* **Beneficio:** Evita interacciones de red innecesarias, mejorando drásticamente la performance.


4. **Uniform Interface (Interfaz Uniforme):**
* Es la restricción central de REST. Define cómo el cliente y el servidor se comunican de forma estandarizada. Implica:
* **Identificación de recursos:** Cada recurso tiene una URI única.
* **Manipulación mediante representaciones:** El cliente modifica el recurso enviando una representación (ej. un JSON).
* **Mensajes auto-descriptivos:** Uso correcto de métodos HTTP y Content-Types.
* **HATEOAS (Hypermedia As The Engine Of Application State):** La API debe proveer links para que el cliente descubra dinámicamente qué acciones puede tomar (opcional en la práctica, pero requerido por la teoría estricta).




5. **Layered System (Sistema en capas):**
* El cliente no sabe (ni le importa) si está conectado directamente al servidor final o si pasa por proxies, balanceadores de carga o API Gateways.


6. **Code on Demand (Código bajo demanda - Opcional):**
* El servidor puede transferir lógica ejecutable al cliente (ej. scripts en JS o applets). Es la única restricción opcional.



---

## 🌍 2️⃣ Recursos y URIs

### 📌 ¿Qué es un Recurso?

Un recurso es cualquier entidad de información (un *Usuario*, un *Pedido*, un *Catálogo*). **Es un sustantivo, nunca un verbo o una acción.**

### 📌 Buenas Prácticas de URI

* **Sustantivos en plural:** `/users`, `/products`.
* **Minúsculas y guiones:** `/user-profiles`, no `/userProfiles` ni `/user_profiles`.
* **Sin verbos en la ruta:** La acción la define el método HTTP.
* **Sin extensiones de formato:** Usar el header `Accept` en lugar de `/users.json`.

| ❌ Incorrecto | ✅ Correcto (RESTful) |
| --- | --- |
| `POST /createUser` | `POST /users` |
| `GET /getUser?id=10` | `GET /users/10` |
| `POST /users/10/delete` | `DELETE /users/10` |
| `GET /users/10/getOrders` | `GET /users/10/orders` |

---

## 🔄 3️⃣ Métodos HTTP y Semántica

El estándar define qué intención tiene cada acción. Un concepto clave aquí es la **Idempotencia**: *Una operación es idempotente si ejecutarla una vez tiene el mismo efecto en el estado del servidor que ejecutarla múltiples veces seguidas.*

* **`GET`**: Recupera una representación del recurso.
* *Seguro* (no muta estado) e *Idempotente*.


* **`POST`**: Crea un nuevo recurso (generalmente subordinado) o procesa datos.
* *No seguro* y *No idempotente* (hacerlo 2 veces crea 2 recursos).


* **`PUT`**: Reemplaza por completo el recurso de destino con la carga útil actual. Si no existe, puede crearlo.
* *No seguro* pero **Sí es idempotente** (mandar el mismo reemplazo 10 veces deja el mismo resultado que mandarlo 1 vez).


* **`PATCH`**: Aplica modificaciones parciales a un recurso.
* *No seguro* y *No necesariamente idempotente* (depende de cómo se implemente la actualización parcial).


* **`DELETE`**: Elimina el recurso especificado.
* *No seguro* pero **Sí es idempotente** (borrar algo que ya está borrado no cambia el estado final).



---

## 📊 4️⃣ Códigos de Estado HTTP

Agrupados por familia para indicar el resultado de la operación:

* **`2xx` (Éxito):**
* `200 OK`: Éxito general (GET, PUT, PATCH).
* `201 Created`: Recurso creado con éxito (POST). Debería incluir un header `Location` con la URI del nuevo recurso.
* `204 No Content`: Éxito, pero no hay body en la respuesta (común en DELETE o PUT).


* **`4xx` (Error del Cliente):**
* `400 Bad Request`: Sintaxis inválida o error de validación del payload.
* `401 Unauthorized`: Falta autenticación o es inválida (ej. token expirado).
* `403 Forbidden`: Autenticado, pero sin permisos para ese recurso.
* `404 Not Found`: La URI no existe o el recurso no fue encontrado.
* `415 Unsupported Media Type`: El servidor no soporta el formato enviado (ej. mandaste XML pero solo acepta JSON).
* `422 Unprocessable Entity`: El JSON está bien formado, pero viola reglas de negocio (ej. un mail duplicado).


* **`5xx` (Error del Servidor):**
* `500 Internal Server Error`: Excepción no manejada en el backend. (Evitar exponer stacktraces).
* `503 Service Unavailable`: Servidor caído o sobrecargado.



---

## 📦 5️⃣ Representaciones y Content Negotiation

REST permite que un mismo recurso se represente de múltiples formas (JSON, XML, PDF). Esto se negocia mediante los **Headers HTTP**:

* **`Content-Type`**: Indica el formato de los datos que el cliente le *envía* al servidor.
* Ej: `Content-Type: application/json`


* **`Accept`**: Indica el/los formatos que el cliente *espera recibir* del servidor.
* Ej: `Accept: application/json, application/xml;q=0.9`



---

## ⚙️ 6️⃣ JAX-RS: El Estándar Java para REST

### 📌 ¿Qué es JAX-RS?

**JAX-RS (Java API for RESTful Web Services)** es una especificación basada en anotaciones para construir APIs REST en Java.
Al ser una especificación (interfaces y reglas), requiere una implementación concreta para funcionar. Las más populares son:

* **Jersey** (Implementación de referencia).
* **RESTEasy** (Usado en JBoss/WildFly y Quarkus).
* **Apache CXF**.

---

## 🏗 7️⃣ Componentes Clave de JAX-RS

### 7.1 Resource Class y Métodos Básicos

Las clases se mapean a URIs mediante la anotación `@Path`.

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON) // Toda la clase devuelve JSON por defecto
@Consumes(MediaType.APPLICATION_JSON) // Toda la clase recibe JSON por defecto
public class UserResource {

    @GET
    public Response getAllUsers() {
        List<User> users = userService.findAll();
        return Response.ok(users).build(); // Retorna 200 OK
    }
}

```

### 7.2 Inyección de Parámetros

JAX-RS provee anotaciones para extraer datos del Request:

```java
    // PATH PARAM: Extrae datos de la URI (/users/123)
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) { ... }

    // QUERY PARAM: Extrae datos de la querystring (/users?role=ADMIN&page=2)
    @GET
    public Response filterUsers(@QueryParam("role") String role, 
                                @DefaultValue("1") @QueryParam("page") int page) { ... }

    // HEADER PARAM: Extrae datos de los headers HTTP
    @GET
    @Path("/me")
    public Response getProfile(@HeaderParam("Authorization") String token) { ... }

```

### 7.3 Response API

En lugar de retornar el objeto directamente, usar la clase `Response` permite un control total sobre el código de estado, los headers y el body.

```java
    @POST
    public Response createUser(UserDTO dto) {
        User createdUser = userService.create(dto);
        URI location = URI.create("/users/" + createdUser.getId());
        
        return Response
                .created(location) // Set HTTP 201 y Header Location
                .entity(createdUser) // Set Response Body
                .build();
    }

```

---

## 🧠 8️⃣ Providers: Filtros y Manejo de Errores

### 📌 ExceptionMapper (Manejo centralizado de errores)

Atrapa excepciones lanzadas en el código y las transforma en respuestas HTTP estandarizadas.

```java
@Provider
public class EntityNotFoundMapper implements ExceptionMapper<EntityNotFoundException> {
    
    @Override
    public Response toResponse(EntityNotFoundException ex) {
        ErrorDTO error = new ErrorDTO("NOT_FOUND", ex.getMessage());
        return Response
                .status(Response.Status.NOT_FOUND) // 404
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

```

### 📌 Filtros (Interceptors del Request/Response)

Ideales para lógica transversal (Logging, CORS, Seguridad).

```java
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
        // Lógica de validación de JWT...
    }
}

```

---

## 🧭 9️⃣ Buenas Prácticas REST + JAX-RS

1. **Separación de Capas (DTOs):** Nunca expongas tus entidades JPA (`@Entity`) directamente en los controllers JAX-RS. Usá DTOs (Data Transfer Objects) para evitar exponer información sensible y evitar bucles infinitos en la serialización JSON.
2. **Validación:** Integrá *Jakarta Bean Validation* (`@Valid`, `@NotNull`, `@Size`) directamente en las firmas de los métodos.
```java
public Response create(@Valid @NotNull UserDTO dto) { ... }

```


3. **Paginación y Filtrado:** Nunca devuelvas colecciones enteras sin límite. Usá `QueryParam` para paginar (`?offset=0&limit=50`).
4. **Versionado de la API:** Anticipate a los "breaking changes".
* Por URI: `/api/v1/users` (Más común y simple).
* Por Header: `Accept: application/vnd.mycompany.v1+json`.



---

## 📌 🔟 La Diferencia: `javax` vs `jakarta`

Históricamente, JAX-RS era parte de **Java EE** bajo el paquete `javax.ws.rs.*`.
Cuando Oracle donó Java EE a la Fundación Eclipse, hubo restricciones legales de marca. El framework pasó a llamarse **Jakarta EE**.

* **Java EE 8 o anterior:** Usa `javax.ws.rs.*`
* **Jakarta EE 9 o superior:** Usa `jakarta.ws.rs.*` (Ej. Spring Boot 3+, Quarkus 3+).
* ⚠️ **Atención:** No son compatibles binariamente. Si usás Spring Boot 3 con bibliotecas viejas que buscan `javax`, vas a tener errores de `ClassNotFoundException` en tiempo de ejecución.

---

¿Qué te parece esta versión? Como me dejaste opciones al final del documento, ¿te gustaría que te genere un ejemplo avanzado enfocado a una arquitectura enterprise (por ejemplo, mostrando cómo implementar un filtro de JWT robusto y versionado real) o preferís explorar cómo integrar esto directamente en un entorno de Spring con Jersey?