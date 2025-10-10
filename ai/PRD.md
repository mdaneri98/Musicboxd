<PRD name="Musicboxd_API_Migration">
  <Overview>
    <Description>
      Migración del sistema Musicboxd (Letterboxd para música) desde una arquitectura Spring MVC con Hibernate hacia una API REST pura,
      que servirá datos JSON a una SPA (Single Page Application) independiente.
    </Description>
    <Goals>
      <Goal>Eliminar capa webapp basada en JSP y ModelAndView</Goal>
      <Goal>Implementar capa API REST que exponga endpoints JSON</Goal>
      <Goal>Usar JWT para autenticación</Goal>
      <Goal>Conservar esquema de base de datos y capa de persistencia actual</Goal>
      <Goal>Actualizar capa de servicios para usar DTOs y mappers manuales</Goal>
      <Goal>Documentar la API con Swagger</Goal>
    </Goals>
  </Overview>

  <Architecture>
    <CurrentStack>
      <Framework>Spring MVC</Framework>
      <ORM>Hibernate</ORM>
      <Server>Tomcat 9</Server>
      <Database>PostgreSQL</Database>
      <BuildSystem>Maven</BuildSystem>
      <Modules>
        <Module name="models" purpose="Entidades JPA y DTOs" />
        <Module name="persistence" purpose="DAO y repositorios Hibernate" />
        <Module name="services" purpose="Lógica de negocio y validación" />
        <Module name="interfaces" purpose="Definición de interfaces de servicios y repositorios" />
        <Module name="webapp" purpose="Interfaz JSP (obsoleta, se eliminará)" />
      </Modules>
    </CurrentStack>
  </Architecture>

  <Database>
    <Engine>PostgreSQL</Engine>
    <Tables>
      <Table name="cuser" purpose="Usuarios registrados con perfil, imagen, contadores, flags de moderación y verificación" />
      <Table name="artist" purpose="Artistas musicales con biografía e imagen" />
      <Table name="album" purpose="Álbumes con metadatos, imagen y artista asociado" />
      <Table name="song" purpose="Canciones con duración, número de pista y álbum" />
      <Table name="review" purpose="Reseñas de usuarios hacia álbumes, artistas o canciones" />
      <Table name="comment" purpose="Comentarios sobre reseñas" />
      <Table name="follower" purpose="Relaciones de seguimiento entre usuarios" />
      <Table name="favorite_artist" purpose="Artistas marcados como favoritos por usuarios" />
      <Table name="favorite_album" purpose="Álbumes favoritos" />
      <Table name="favorite_song" purpose="Canciones favoritas" />
      <Table name="review_like" purpose="Likes de usuarios sobre reseñas" />
      <Table name="image" purpose="Imágenes en formato binario (BYTEA)" />
      <Table name="verification" purpose="Códigos de verificación o recuperación de cuenta" />
      <Table name="artist_review" purpose="Relación Review-Artist" />
      <Table name="album_review" purpose="Relación Review-Album" />
      <Table name="song_review" purpose="Relación Review-Song" />
    </Tables>
    <Features>
      <Feature>Auditoría con timestamps created_at y updated_at</Feature>
      <Feature>Relaciones ManyToMany: followers, favoritos, likes</Feature>
    </Features>
  </Database>

  <ServicesLayer>
    <Structure>
      <Pattern>Interface + Implementation</Pattern>
      <Transactional>true</Transactional>
      <Responsibilities>
        <Item>Validación de reglas de negocio</Item>
        <Item>Interacción con la capa de persistencia</Item>
        <Item>Conversión entidad ↔ DTO</Item>
        <Item>Paginación y filtros</Item>
      </Responsibilities>
    </Structure>
    <DTOs>
      <Existing>Usuario, Artista, Álbum, Canción</Existing>
      <Pending>Review, Comment, Relaciones (followers, favorites, likes)</Pending>
    </DTOs>
    <Mapping>Manual</Mapping>
  </ServicesLayer>

  <API>
    <Framework>Spring Web (REST Controllers)</Framework>
    <Serialization>JSON</Serialization>
    <Authentication>JWT</Authentication>
    <Pagination>Soportada por capa de servicios y persistencia</Pagination>
    <Documentation>Swagger / OpenAPI</Documentation>
    <Endpoints>
      <Resource name="Users" path="/api/users" />
      <Resource name="Users" path="/api/users/{id}" />
      <Resource name="Users" path="/api/users/{id}/reviews" />
      <Resource name="Users" path="/api/users/{id}/following" />
      <Resource name="Users" path="/api/users/{id}/followers" />
      <Resource name="Users" path="/api/users/{id}/favorites" />
      <Resource name="Auth" path="/api/auth" />
      <Resource name="Artists" path="/api/artists" />
      <Resource name="Artists" path="/api/artists/{id}" />
      <Resource name="Artists" path="/api/artists/{id}/reviews" />
      <Resource name="Albums" path="/api/albums" />
      <Resource name="Albums" path="/api/albums/{id}" />
      <Resource name="Albums" path="/api/albums/{id}/reviews" />
      <Resource name="Songs" path="/api/songs" />
      <Resource name="Songs" path="/api/songs/{id}" />
      <Resource name="Songs" path="/api/songs/{id}/reviews" />
      <Resource name="Reviews" path="/api/reviews" />
      <Resource name="Reviews" path="/api/reviews/{id}" />
      <Resource name="Reviews" path="/api/reviews/{id}/comments" />
      <Resource name="Reviews" path="/api/reviews/{id}/likes" />
      <Resource name="Images" path="/api/images" />
      <Resource name="Images" path="/api/images/{id}" />
    </Endpoints>
    <ResponseFormat>
      <Type>JSON plano</Type>
      <Example>
{
"data": {
"id": 4,
"username": "paulmcc",
"email": "paulmcc@example.com",
"name": "Paul McCartney",
"bio": "Member of The Beatles",
"image_id": 1,
"followers_amount": 0,
"following_amount": 0,
"reviews_amount": 0,
"created_at": "2024-09-10T00:28:52.621277",
"updated_at": "2024-09-10T00:28:52.621277",
"preferred_language": "es",
"preferred_theme": "dark",
"has_follow_notifications_enabled": true,
"has_like_notifications_enabled": true,
"has_comments_notifications_enabled": true,
"has_reviews_notifications_enabled": true,
"moderator": false,
"verified": false
},
"_links": [
{
"href": "http://localhost:8080/api_war/users/4",
"rel": "self",
"title": null,
"type": null,
"method": null
},
{
"href": "http://localhost:8080/api_war/users/4",
"rel": "edit",
"title": "Edit this resource",
"type": "application/json",
"method": "PUT"
},
{
"href": "http://localhost:8080/api_war/users/4",
"rel": "delete",
"title": "Delete this resource",
"type": null,
"method": "DELETE"
},
{
"href": "reviews",
"rel": "http://localhost:8080/api_war/users/4/reviews",
"title": null,
"type": null,
"method": null
},
{
"href": "followers",
"rel": "http://localhost:8080/api_war/users/4/followers",
"title": null,
"type": null,
"method": null
},
{
"href": "following",
"rel": "http://localhost:8080/api_war/users/4/following",
"title": null,
"type": null,
"method": null
}
],
"_embedded": null
}
      </Example>
    </ResponseFormat>
  </API>

  <MigrationPlan>
    <Phase name="1. Refactorización de Servicios">
      <Task>Actualizar servicios para devolver DTOs en lugar de entidades</Task>
      <Task>Implementar mappers manuales entidad↔DTO</Task>
      <Task>Agregar DTOs faltantes (Review, Comment, Follow, Favorites, etc)</Task>
    </Phase>
    <Phase name="2. Implementación de Capa API REST">
      <Task>Crear controladores REST por entidad principal</Task>
      <Task>Mapear endpoints a métodos de servicio existentes</Task>
      <Task>Definir rutas RESTful siguiendo convención CRUD</Task>
      <Task>Agregar manejo global de excepciones (ControllerAdvice)</Task>
      <Task>Configurar CORS abierto temporalmente (para SPA futura)</Task>
    </Phase>
    <Phase name="3. Autenticación JWT">
      <Task>Agregar endpoint /api/auth/login y /api/auth/register</Task>
      <Task>Emitir token JWT con claims mínimos (userId, username, roles)</Task>
      <Task>Agregar filtros de autorización a rutas protegidas</Task>
    </Phase>
    <Phase name="4. Documentación y Testing">
      <Task>Configurar Swagger UI</Task>
      <Task>Actualizar tests unitarios existentes</Task>
      <Task>Agregar pruebas de integración para endpoints REST</Task>
    </Phase>
    <Phase name="5. Despliegue y Deprecación">
      <Task>Eliminar módulo webapp (JSP, controladores viejos)</Task>
      <Task>Configurar entorno Docker para backend REST</Task>
      <Task>Preparar base para integración con SPA</Task>
    </Phase>
  </MigrationPlan>

  <Frontend>
    <Type>SPA (Single Page Application)</Type>
    <Framework>TBD (React, Vue o similar)</Framework>
    <DataFlow>Consumo directo de API REST mediante JWT</DataFlow>
    <Routes>
      <Route>/feed</Route>
      <Route>/profile/:username</Route>
      <Route>/album/:id</Route>
      <Route>/artist/:id</Route>
      <Route>/review/:id</Route>
      <Route>/login</Route>
      <Route>/register</Route>
    </Routes>
  </Frontend>

  <NonFunctionalRequirements>
    <Testing>JUnit unit tests</Testing>
    <Logging>Spring logs + futuras auditorías</Logging>
    <Stateless>true</Stateless>
    <Compatibility>Esquema DB actual se mantiene</Compatibility>
    <Performance>Endpoints paginados y optimizados para fetch selectivo</Performance>
  </NonFunctionalRequirements>
</PRD>
