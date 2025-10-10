<Planning>
  <Project name="Musicboxd_API_Migration">
    <Vision>
      Migrar Musicboxd (Letterboxd para música) de Spring MVC con Hibernate y JSP
      a una API REST pura que entregue JSON a una SPA moderna e independiente.
    </Vision>

    <Goals>
      <Goal>Eliminar la capa JSP y ModelAndView</Goal>
      <Goal>Exponer una API RESTful completa</Goal>
      <Goal>Reutilizar la capa de persistencia existente</Goal>
      <Goal>Implementar autenticación JWT</Goal>
      <Goal>Usar DTOs y mappers manuales</Goal>
      <Goal>Documentar con Swagger/OpenAPI</Goal>
      <Goal>Preparar backend para consumo por SPA</Goal>
    </Goals>

    <Architecture>
      <Backend>Spring Boot (Web, Security, Data JPA)</Backend>
      <ORM>Hibernate</ORM>
      <Database>PostgreSQL</Database>
      <Auth>JWT</Auth>
      <Docs>Swagger / OpenAPI</Docs>
      <Server>Tomcat 9</Server>
      <Build>Maven</Build>
      <Frontend>SPA (React o Vue)</Frontend>
    </Architecture>

    <Modules>
      <Module name="models" status="done">Entidades JPA y DTOs</Module>
      <Module name="persistence" status="done">Repositorios Hibernate</Module>
      <Module name="services" status="pending">Lógica de negocio, validaciones, DTOs</Module>
      <Module name="interfaces" status="done">Interfaces de servicios y repositorios</Module>
      <Module name="webapp" status="deprecated">Capa JSP antigua</Module>
      <Module name="api" status="in_progress">Controladores REST</Module>
    </Modules>

    <Database>
      <Engine>PostgreSQL</Engine>
      <Notes>
        Auditoría con timestamps, relaciones N:N (followers, likes, favoritos),
        tablas principales: cuser, artist, album, song, review, comment, follower, favorite, verification.
      </Notes>
    </Database>

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

    <Phases>
      <Phase name="1 - Refactorización de Servicios">
        <Task>Devolver DTOs en lugar de entidades</Task>
        <Task>Agregar mappers manuales</Task>
        <Task>Completar DTOs de Review, Comment, Follow, Favorites. Los DTOs se encuentran en el modulo Models</Task>
      </Phase>

      <Phase name="2 - Implementación de API REST">
        <Task>Crear controladores REST</Task>
        <Task>Mapear endpoints a servicios</Task>
        <Task>Agregar manejo global de excepciones</Task>
        <Task>Configurar CORS para desarrollo</Task>
      </Phase>

      <Phase name="3 - Autenticación JWT">
        <Task>Crear /auth/login y /auth/register</Task>
        <Task>Emitir y validar tokens</Task>
        <Task>Proteger rutas privadas</Task>
      </Phase>

      <Phase name="4 - Documentación y Testing">
        <Task>Configurar Swagger UI</Task>
        <Task>Agregar tests de integración</Task>
        <Task>Actualizar tests unitarios</Task>
      </Phase>

      <Phase name="5 - Despliegue">
        <Task>Eliminar webapp JSP</Task>
        <Task>Dockerizar backend REST</Task>
        <Task>Preparar integración con SPA</Task>
      </Phase>
    </Phases>

    <Workflow>
      <Rule>Mantain current architecture and format</Rule>
      <Rule>Always read PLANNING.md at the start of every session</Rule>
      <Rule>Check TASKS.md before starting work</Rule>
      <Rule>Mark completed tasks immediately</Rule>
      <Rule>Add new tasks to TASKS.md every time</Rule>
      <Rule>Maintain consistency between layers (API → Service → Persistence)</Rule>
      <Rule>Do not modify database schema unless authorized</Rule>
    </Workflow>

    <ExpectedResult>
      API REST funcional y documentada, sin dependencia de JSP.
      Servicios desacoplados, autenticación JWT activa, lista para SPA.
    </ExpectedResult>
  </Project>
</Planning>
