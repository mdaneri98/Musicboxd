package ar.edu.itba.paw.api.exception;

import ar.edu.itba.paw.models.dtos.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Manejador global de excepciones para la API REST (catch-all).
 *
 * Este mapper actúa como último recurso para capturar cualquier excepción
 * que no haya sido manejada por los mappers especializados.
 *
 * Mappers especializados (tienen prioridad):
 * - EntityNotFoundExceptionMapper: Excepciones 404
 * - ConflictExceptionMapper: Excepciones 409
 * - ValidationExceptionMapper: Errores de validación 400
 * - BadRequestExceptionMapper: Errores 400
 * - EmailExceptionMapper: Errores de email 500
 * - WebApplicationExceptionMapper: Excepciones JAX-RS
 *
 * Este handler solo se ejecuta si ningún otro mapper procesó la excepción.
 *
 * Siguiendo las mejores prácticas:
 * - Single Responsibility Principle: Solo maneja excepciones genéricas
 * - Open/Closed Principle: Extensible mediante nuevos mappers sin modificar este
 * - Separation of Concerns: Cada mapper maneja un tipo específico
 * - DRY: ErrorResponseBuilder centraliza la construcción de respuestas
 */
@Provider
@Component
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        // Log con nivel ERROR porque cualquier excepción que llegue aquí
        // es inesperada y debería ser investigada
        LOGGER.error("Unhandled exception occurred: {}", exception.getClass().getName(), exception);

        // Construir respuesta de error genérica
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                uriInfo.getPath()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}
