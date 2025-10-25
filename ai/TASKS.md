<Tasks>
  <Task name="Setup_API_Project" status="done" priority="high">
    <Detail>Inicializar módulo API dentro del monorepo de Musicboxd.</Detail>
    <Subtask name="Crear_estructura_base" status="done">
      <Detail>Configurar Spring con dependencias esenciales: spring-web, spring-security, jackson, hibernate.</Detail>
    </Subtask>
    <Subtask name="Integrar_servicios" status="done">
      <Detail>Conectar capa API con capa de servicios existente.</Detail>
    </Subtask>
    <Subtask name="Verificar_conexion_DB" status="done">
      <Detail>Confirmar acceso correcto a la base de datos mediante capa de persistencia.</Detail>
    </Subtask>
  </Task>

  <Task name="DTO_Review" status="done" priority="medium">
    <Detail>Revisar consistencia y privacidad de los DTOs.</Detail>
    <Subtask name="Verificar_existencia" status="done">
      <Detail>Confirmar que todas las entidades del dominio tengan DTO correspondiente.</Detail>
      <Note>DTOs creados: ReviewDTO, CommentDTO, NotificationDTO. Actualizados: ArtistDTO, AlbumDTO, SongDTO, UserDTO.</Note>
    </Subtask>
    <Subtask name="Agregar_mappers" status="done">
      <Detail>Crear mappers con ModelMapper o funciones manuales según necesidad.</Detail>
      <Note>Mappers creados: ArtistMapper, AlbumMapper, SongMapper, ReviewMapper, CommentMapper, NotificationMapper, UserMapper.</Note>
    </Subtask>
    <Subtask name="Revisar_seguridad" status="done">
      <Detail>Eliminar campos sensibles de los DTOs (tokens, contraseñas, IDs internos).</Detail>
      <Note>DTOs actualizados para no exponer datos sensibles. Password no se incluye en UserDTO.</Note>
    </Subtask>
  </Task>

  <Task name="HATEOAS_Implementation" status="done" priority="high">
    <Detail>Implementar capa HATEOAS con Resources y ResourceMappers.</Detail>
    <Subtask name="Crear_Resources" status="done">
      <Detail>Crear wrappers Resource para cada entidad del dominio.</Detail>
      <Note>Resources creados: UserResource, ArtistResource, AlbumResource, SongResource, ReviewResource, CommentResource. ReviewResource actualizado para usar ReviewDTO.</Note>
    </Subtask>
    <Subtask name="Crear_ResourceMappers" status="done">
      <Detail>Implementar mappers que conviertan DTOs a Resources con links HATEOAS.</Detail>
      <Note>ResourceMappers creados para User, Artist, Album, Song, Review, Comment. Todos refactorizados para usar LinkManagers.</Note>
    </Subtask>
    <Subtask name="Agregar_Links" status="done">
      <Detail>Agregar links de navegación (self, related, actions) a cada Resource.</Detail>
      <Note>Links implementados: self, edit, delete, collection, y links a recursos relacionados. LinkManagers creados: UserLinkManager, ArtistLinkManager, AlbumLinkManager, SongLinkManager, ReviewLinkManager, CommentLinkManager.</Note>
    </Subtask>
    <Subtask name="Crear_LinkManagers" status="done">
      <Detail>Crear LinkManagers centralizados para generar links HATEOAS.</Detail>
      <Note>LinkManagers implementados para todas las entidades (User, Artist, Album, Song, Review, Comment). UriBuilder extendido con métodos para construir URIs de todas las entidades.</Note>
    </Subtask>
  </Task>

  <Task name="Controllers_Migration" status="done" priority="high">
    <Detail>Migrar controladores de Spring MVC a controladores RESTful.</Detail>
    <Subtask name="Retornar_JSON" status="done">
      <Detail>Convertir respuestas en objetos JSON usando ResponseEntity y DTOs.</Detail>
      <Note>Controladores REST implementados: UserController, ArtistController, AlbumController, SongController, ReviewController, CommentController.</Note>
    </Subtask>
    <Subtask name="Implementar_CRUD" status="done">
      <Detail>Endpoints CRUD completos para todas las entidades principales.</Detail>
      <Note>GET, POST, PUT, DELETE implementados con Resources y HATEOAS.</Note>
    </Subtask>
    <Subtask name="Endpoints_Relacionados" status="done">
      <Detail>Implementar endpoints para relaciones (reviews, comments, songs, etc).</Detail>
      <Note>Endpoints: /artists/{id}/reviews, /albums/{id}/songs, /reviews/{id}/comments, etc.</Note>
    </Subtask>
  </Task>

  <Task name="Service_Layer_Refactoring" status="pending" priority="high">
    <Detail>Refactorizar servicios para devolver DTOs en lugar de entidades.</Detail>
    <Subtask name="Actualizar_ArtistService" status="pending">
      <Detail>Métodos deben devolver ArtistDTO y usar nuevos DTOs sin campos obsoletos.</Detail>
    </Subtask>
    <Subtask name="Actualizar_AlbumService" status="pending">
      <Detail>Métodos deben devolver AlbumDTO y eliminar uso de campos obsoletos.</Detail>
    </Subtask>
    <Subtask name="Actualizar_SongService" status="pending">
      <Detail>Métodos deben devolver SongDTO y eliminar uso de campos obsoletos.</Detail>
    </Subtask>
    <Subtask name="Actualizar_ReviewService" status="pending">
      <Detail>Métodos deben devolver ReviewDTO y usar ReviewMapper.</Detail>
    </Subtask>
    <Subtask name="Actualizar_CommentService" status="pending">
      <Detail>Métodos deben devolver CommentDTO y usar CommentMapper.</Detail>
    </Subtask>
  </Task>

  <Task name="Global_Exception_Handler" status="done" priority="medium">
    <Detail>Unificar manejo de errores y respuestas JSON estándar.</Detail>
    <Subtask name="Crear_ExceptionMappers" status="done">
      <Detail>Implementar ExceptionMappers JAX-RS para cada tipo de excepción.</Detail>
      <Note>ExceptionMappers creados: EntityNotFoundExceptionMapper, ConflictExceptionMapper, BadRequestExceptionMapper, EmailExceptionMapper, ValidationExceptionMapper, WebApplicationExceptionMapper, GlobalExceptionHandler. Todos configurados con @Provider y @Component para integración Jersey-Spring.</Note>
    </Subtask>
    <Subtask name="Definir_error_schema" status="done">
      <Detail>Formato: timestamp, code, message, path.</Detail>
      <Note>ErrorResponseDTO implementado con código, status, mensaje, path y validationErrors opcionales.</Note>
    </Subtask>
    <Subtask name="Configurar_Jersey_Scanning" status="done">
      <Detail>Agregar paquetes de exception mappers al web.xml para que Jersey los detecte.</Detail>
      <Note>web.xml actualizado para incluir ar.edu.itba.paw.api.exception.mapper y ar.edu.itba.paw.api.exception</Note>
    </Subtask>
  </Task>

  <Task name="Security_Implementation" status="pending" priority="high">
    <Detail>Implementar autenticación basada en JWT con Spring Security.</Detail>
    <Subtask name="Configurar_JWT" status="pending">
      <Detail>Generar tokens al iniciar sesión y validar en requests protegidos.</Detail>
    </Subtask>
    <Subtask name="Endpoints_Auth" status="pending">
      <Detail>Crear AuthController con /auth/login y /auth/register.</Detail>
    </Subtask>
    <Subtask name="Endpoints_Publicos" status="pending">
      <Detail>Definir endpoints públicos (login, register) y protegidos en SecurityConfig.</Detail>
    </Subtask>
    <Subtask name="Middleware_Token" status="pending">
      <Detail>Agregar filtro que valide tokens antes de llegar al controlador.</Detail>
    </Subtask>
  </Task>

  <Task name="CORS_and_Config" status="pending" priority="low">
    <Subtask name="Configurar_CORS" status="pending">
      <Detail>Permitir acceso desde dominio de la SPA en desarrollo.</Detail>
    </Subtask>
    <Subtask name="Verificar_headers" status="pending">
      <Detail>Autorizar métodos y encabezados HTTP adecuados para JWT.</Detail>
    </Subtask>
  </Task>

  <Task name="Swagger_Documentation" status="pending" priority="medium">
    <Detail>Exponer documentación con OpenAPI/Swagger.</Detail>
    <Subtask name="Integrar_dependencia" status="pending">
      <Detail>Agregar springdoc-openapi-ui en pom.xml.</Detail>
    </Subtask>
    <Subtask name="Documentar_endpoints" status="pending">
      <Detail>Agregar anotaciones @Operation, @ApiResponse en controladores.</Detail>
    </Subtask>
    <Subtask name="Configurar_Swagger_UI" status="pending">
      <Detail>Accesible en /swagger-ui.html con información de la API.</Detail>
    </Subtask>
  </Task>

  <Task name="Testing" status="pending" priority="medium">
    <Detail>Validar servicios y controladores con tests automatizados.</Detail>
    <Subtask name="Unit_tests" status="pending">
      <Detail>Implementar JUnit + Mockito para pruebas unitarias de servicios.</Detail>
    </Subtask>
    <Subtask name="Integration_tests" status="pending">
      <Detail>Probar endpoints REST con Jersey Test Framework.</Detail>
    </Subtask>
    <Subtask name="Postman_collection" status="pending">
      <Detail>Crear colección de pruebas API manuales con todos los endpoints.</Detail>
    </Subtask>
  </Task>

  <Task name="Frontend_Integration" status="pending" priority="high">
    <Detail>Conectar la SPA con el backend REST.</Detail>
    <Subtask name="Verificar_endpoints" status="pending">
      <Detail>Confirmar todas las rutas REST accesibles desde frontend.</Detail>
    </Subtask>
    <Subtask name="Testear_CORS_y_auth" status="pending">
      <Detail>Probar autenticación JWT y requests CORS desde el cliente.</Detail>
    </Subtask>
    <Subtask name="Validar_HATEOAS" status="pending">
      <Detail>Verificar que el frontend pueda navegar usando los links HATEOAS.</Detail>
    </Subtask>
  </Task>

  <Task name="Deployment_and_Docs" status="pending" priority="low">
    <Detail>Preparar despliegue y documentación final.</Detail>
    <Subtask name="Perfiles_deploy" status="pending">
      <Detail>Definir perfiles dev, staging y prod en application.properties.</Detail>
    </Subtask>
    <Subtask name="Documentacion_final" status="pending">
      <Detail>Actualizar README con pasos para levantar API y consumir endpoints.</Detail>
    </Subtask>
    <Subtask name="Docker_Setup" status="pending">
      <Detail>Crear Dockerfile y docker-compose.yml para backend + PostgreSQL.</Detail>
    </Subtask>
  </Task>

  <Task name="Current_Progress_Summary" status="info" priority="info">
    <Detail>Resumen del estado actual del proyecto (Octubre 2024).</Detail>
    <Note>
      ✅ COMPLETADO:
      - DTOs completos para todas las entidades (User, Artist, Album, Song, Review, Comment, Notification)
      - Mappers de servicios (Entity ↔ DTO) para todas las entidades
      - Capa HATEOAS: Resources y ResourceMappers para API REST
      - 6 Controladores REST completos con CRUD y relaciones
      - Estructura de URIs consistente y RESTful
      - CollectionResource con soporte para paginación
      
      🔄 EN PROGRESO:
      - Actualización de servicios para usar nuevos DTOs (eliminar campos obsoletos)
      
      ⏳ PENDIENTE:
      - Autenticación JWT y Spring Security
      - Manejo global de excepciones
      - Documentación Swagger/OpenAPI
      - Tests unitarios e integración
      - CORS para SPA
      - Deployment con Docker
    </Note>
  </Task>
</Tasks>
