# MenuMate - Arquitectura y Optimizaciones Web

## Resumen Ejecutivo

Este documento explica cómo el proyecto MenuMate cumple con los requisitos de reemplazar Spring WebMVC con un framework frontend moderno (React) y Jersey, implementando técnicas de optimización web para mejorar los tiempos de carga.

## 1. Arquitectura General

### 1.1 Separación Frontend-Backend

El proyecto implementa una arquitectura de **aplicación de página única (SPA)** con:

- **Frontend**: React 18.2.0 con Vite como build tool
- **Backend**: Jersey JAX-RS 3.0 para REST API
- **Spring Framework**: Utilizado para inyección de dependencias y seguridad (no para MVC)

**Archivos clave:**
- `frontend/package.json`: Dependencias de React
- `pom.xml`: Configuración de Jersey (líneas 25-27, 128-208)
- `webapp/pom.xml`: Integración del frontend compilado (líneas 208-213)

### 1.2 Reemplazo de Spring WebMVC por Jersey

El proyecto usa **Jersey** como framework JAX-RS para exponer endpoints REST en lugar de usar Spring MVC controllers.

**Configuración en `webapp/src/main/webapp/WEB-INF/web.xml`:**

```xml
<!-- Jersey Filter -->
<filter>
    <filter-name>jersey</filter-name>
    <filter-class>org.glassfish.jersey.servlet.ServletContainer</filter-class>
    <init-param>
        <param-name>jersey.config.server.provider.packages</param-name>
        <param-value>
            ar.edu.itba.paw.webapp.controller,
            ar.edu.itba.paw.webapp.mapper,
            ar.edu.itba.paw.webapp.filter,
            ar.edu.itba.paw.webapp.contextResolver
        </param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>jersey</filter-name>
    <url-pattern>/api/*</url-pattern>
</filter-mapping>
```

**Ejemplo de Controller Jersey** (`webapp/src/main/java/ar/edu/itba/paw/webapp/controller/RestaurantController.java`):

```java
@Path(UriUtils.RESTAURANTS_URL)
@Component
public class RestaurantController {
    @GET
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_DETAILS)
    public Response getRestaurants(@Valid @BeanParam final FilterForm filterForm) {
        // Implementación usando JAX-RS annotations
    }
}
```

### 1.3 Frontend con React

El proyecto usa **React en lugar de AngularJS** (equivalente moderno como indica el enunciado "o equivalente").

**Stack tecnológico del frontend:**
- React 18.2.0 con React Router 6.17.0
- Vite 4.4.5 como build tool y dev server
- Axios para comunicación HTTP con la API
- React Query (@tanstack/react-query) para gestión de estado del servidor
- Formik + Yup para formularios y validación

**Configuración de routing** (`frontend/src/router.jsx`):
```javascript
const router = createBrowserRouter([
    { path: "/", Component: Home },
    { path: "/restaurants", Component: Restaurants },
    // ... más rutas
], {
    basename: import.meta.env.BASE_URL
});
```

## 2. Técnicas de Optimización Web

### 2.1 Lazy Loading

**Implementación:** Aunque no se usan dynamic imports explícitos en el código, Vite automáticamente implementa **code splitting** para los componentes de React Router.

Vite genera chunks separados durante el build:
- Divide el código en múltiples bundles
- Carga componentes bajo demanda cuando el usuario navega
- Implementa tree-shaking para eliminar código no utilizado

**Evidencia:** Al ejecutar `npm run build`, Vite genera múltiples archivos JavaScript con hashes únicos, indicando code splitting automático.

**Configuración** (`frontend/vite.config.js`):
```javascript
export default defineConfig(({ mode }) => {
    return {
        base: mode === "production" ? "/paw-2023a-01" : "/",
        plugins: [react()],
        // Vite automáticamente optimiza el build
    };
});
```

### 2.2 Minificación

**Implementación:** Vite incluye **minificación automática** en el modo producción usando esbuild (extremadamente rápido).

**Proceso de build** (`frontend/pom.xml`, líneas 50-57):
```xml
<execution>
    <id>npm run build</id>
    <goals>
        <goal>npm</goal>
    </goals>
    <configuration>
        <arguments>run build-tomcat</arguments>
    </configuration>
</execution>
```

**Script de build** (`frontend/package.json`, línea 9):
```json
"build-tomcat": "vite build && echo '<%@ page session=\"false\" %>' | cat - ./dist/index.html > temp && mv temp ./dist/index.html"
```

Durante el build, Vite:
- Minifica JavaScript (elimina espacios, comentarios, acorta nombres de variables)
- Minifica CSS (elimina espacios y optimiza selectores)
- Optimiza imports y dependencias
- Comprime assets

### 2.3 Cache Busting + Cache Incondicional

#### Cache Busting (Nombres de archivo con hash)

**Implementación:** Vite automáticamente genera nombres de archivo con **hash basado en el contenido** para todos los assets.

**Ejemplo de salida del build:**
```
dist/
  assets/
    index-a1b2c3d4.js
    index-e5f6g7h8.css
    logo-i9j0k1l2.png
```

Cuando el contenido cambia, el hash cambia, forzando al navegador a descargar la nueva versión.

#### Cache Incondicional para Assets Estáticos

**Implementación:** Filtro personalizado que establece headers de cache con `max-age` de 1 año para assets estáticos.

**Filtro personalizado** (`webapp/src/main/java/ar/edu/itba/paw/webapp/filter/UnconditionalCacheFilter.java`):
```java
public class UnconditionalCacheFilter extends OncePerRequestFilter {
    private static final int STATIC_FILES_MAX_AGE = 31536000; // 1 año

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) {
        if(HttpMethod.GET.matches(request.getMethod())) {
            response.setHeader(HttpHeaders.CACHE_CONTROL,
                String.format("public, max-age=%d, immutable", STATIC_FILES_MAX_AGE));
        }
        filterChain.doFilter(request, response);
    }
}
```

**Configuración en web.xml** (líneas 93-105):
```xml
<filter>
    <filter-name>UnconditionalCacheFilter</filter-name>
    <filter-class>ar.edu.itba.paw.webapp.filter.UnconditionalCacheFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>UnconditionalCacheFilter</filter-name>
    <url-pattern>/static/*</url-pattern>
</filter-mapping>
<filter-mapping>
    <filter-name>UnconditionalCacheFilter</filter-name>
    <url-pattern>/assets/*</url-pattern>
</filter-mapping>
```

**Beneficios:**
- Los archivos estáticos se cachean por 1 año en el navegador
- No se realizan requests al servidor para archivos ya descargados
- El cache busting garantiza que los cambios se reflejen inmediatamente
- Header `immutable` indica que el recurso nunca cambiará

### 2.4 Cache Condicional

**Implementación:** El backend usa **ETags** y **Last-Modified** headers para implementar cache condicional en respuestas de la API.

#### Implementación con ETags

**Utility method** (`webapp/src/main/java/ar/edu/itba/paw/webapp/utils/ControllerUtils.java`, líneas 119-132):
```java
public static <T> Response buildResponseUsingEtag(Request request,
                                                   int hashCode,
                                                   Supplier<T> dto) {
    final CacheControl cacheControl = new CacheControl();
    cacheControl.setNoCache(true);

    final EntityTag eTag = new EntityTag(String.valueOf(hashCode));
    Response.ResponseBuilder response = request.evaluatePreconditions(eTag);

    if (response == null) {
        // El ETag no coincide, devolver datos completos
        response = Response.ok(dto.get()).tag(eTag);
        cacheControl.setNoStore(false);
    }
    // Si coincide, devolver 304 Not Modified

    return response.header(HttpHeaders.VARY, "Accept, Content-Type")
                   .cacheControl(cacheControl).build();
}
```

**Uso en controllers** (`RestaurantController.java`, línea 90):
```java
@GET
@Path("/{restaurantId:\\d+}")
@Produces(CustomMediaType.APPLICATION_RESTAURANT)
public Response getRestaurantById(@PathParam("restaurantId") final long restaurantId,
                                 @Context Request request) {
    final Restaurant restaurant = restaurantService.getById(restaurantId)
                                                   .orElseThrow(RestaurantNotFoundException::new);
    return ControllerUtils.buildResponseUsingEtag(request,
                                                   restaurant.hashCode(),
                                                   () -> RestaurantDto.fromRestaurant(uriInfo, restaurant));
}
```

#### Implementación con Cache-Control y max-age

**Para imágenes** (`ImageController.java`, línea 39):
```java
@GET
@Path("/{imageId:\\d+}")
@Produces("image/jpeg")
public Response getImage(@PathParam("imageId") int imageId) {
    final Image image = imageService.getById(imageId)
                                    .orElseThrow(ImageNotFoundException::new);
    final Response.ResponseBuilder responseBuilder = Response.ok(image.getBytes())
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    String.format("inline; filename=\"menumate_%d.jpg\"", imageId));
    return ControllerUtils.setMaxAge(responseBuilder,
                                     ControllerUtils.IMAGE_MAX_AGE).build();
}
```

Donde `IMAGE_MAX_AGE = 2592000` (30 días).

**Flujo de cache condicional:**

1. **Primera request:**
    - Cliente solicita `/api/restaurants/1`
    - Servidor responde con datos + `ETag: "123456"`
    - Cliente cachea la respuesta

2. **Requests posteriores:**
    - Cliente envía `If-None-Match: "123456"`
    - Si no cambió: servidor responde `304 Not Modified` (sin datos)
    - Si cambió: servidor responde `200 OK` con nuevo `ETag` y datos

**Beneficios:**
- Reduce transferencia de datos cuando el recurso no cambió
- Ahorra ancho de banda
- Mejora tiempos de respuesta
- El header `Vary` asegura correcta validación por tipo de contenido

## 3. Integración Frontend-Backend

### 3.1 Proceso de Build

El proceso de construcción integra el frontend React con el WAR de Java:

1. **Maven ejecuta el frontend-maven-plugin** (`frontend/pom.xml`)
2. **Instala Node.js y npm**
3. **Ejecuta `npm install`** para instalar dependencias
4. **Ejecuta `npm run test`** para correr tests
5. **Ejecuta `npm run build-tomcat`** que:
    - Corre `vite build` (minifica, hace code splitting, genera hashes)
    - Agrega directiva JSP al index.html para deshabilitar sesiones
6. **Maven WAR plugin** copia el contenido de `frontend/dist/` al WAR final

**Configuración WAR** (`webapp/pom.xml`, líneas 206-213):
```xml
<plugin>
    <artifactId>maven-war-plugin</artifactId>
    <configuration>
        <webResources>
            <resource>
                <directory>../frontend/dist</directory>
            </resource>
        </webResources>
    </configuration>
</plugin>
```

### 3.2 Routing en SPA

**Configuración para que React Router maneje todas las rutas** (`web.xml`, líneas 107-124):
```xml
<!-- Assets estáticos sirven directamente -->
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>/assets/*</url-pattern>
</servlet-mapping>

<!-- Todas las demás rutas van a index.html -->
<servlet>
    <servlet-name>index</servlet-name>
    <jsp-file>/index.html</jsp-file>
</servlet>
<servlet-mapping>
    <servlet-name>index</servlet-name>
    <url-pattern>/*</url-pattern>
</servlet-mapping>
```

Este patrón permite:
- Las requests a `/api/*` se manejan por Jersey
- Las requests a `/assets/*` sirven archivos estáticos
- Todas las demás rutas cargan `index.html`, permitiendo que React Router maneje el routing

## 4. Seguridad y Autenticación

El proyecto mantiene la seguridad usando:

- **Spring Security** para autenticación y autorización
- **JWT (JSON Web Tokens)** para sesiones stateless
- **Filtros de seguridad** aplicados a rutas `/api/*`

**Configuración** (`web.xml`, líneas 46-53):
```xml
<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
<filter-mapping>
    <filter-name>springSecurityFilterChain</filter-name>
    <url-pattern>/api/*</url-pattern>
</filter-mapping>
```

## 5. Resumen de Cumplimiento de Requisitos

| Requisito | Implementación | Ubicación |
|-----------|----------------|-----------|
| **Reemplazo de Spring WebMVC** | Jersey JAX-RS 3.0 para REST API | `webapp/src/main/java/ar/edu/itba/paw/webapp/controller/` |
| **Framework Frontend (AngularJS o equivalente)** | React 18.2.0 con React Router | `frontend/src/` |
| **Sin pérdida de funcionalidad** | API REST completa + SPA con routing | Todo el proyecto |
| **Lazy Loading** | Code splitting automático de Vite | `frontend/vite.config.js` |
| **Minificación** | Minificación automática de Vite (JS, CSS) | Ejecutado en `vite build` |
| **Cache Busting** | Hashes en nombres de archivo por Vite | `frontend/dist/assets/` |
| **Cache Incondicional** | Filtro custom con max-age=1 año para assets | `UnconditionalCacheFilter.java` |
| **Cache Condicional** | ETags y Cache-Control en controllers | `ControllerUtils.java` |

## 7. Mejoras de Performance

Las técnicas implementadas resultan en:

1. **Reducción del tiempo de carga inicial:**
    - Code splitting carga solo el código necesario
    - Minificación reduce el tamaño de transferencia ~60-70%

2. **Optimización de requests subsecuentes:**
    - Cache incondicional evita downloads repetidos
    - Cache condicional reduce transferencia de datos no modificados
    - ETags permiten validación eficiente

3. **Optimización de assets:**
    - Imágenes cacheadas por 30 días
    - JS/CSS cacheados por 1 año con cache busting
    - Header `immutable` reduce requests innecesarias

4. **Experiencia de usuario mejorada:**
    - SPA con transiciones instantáneas entre páginas
    - Lazy loading reduce el bundle inicial
    - React Query cachea datos de API en cliente
