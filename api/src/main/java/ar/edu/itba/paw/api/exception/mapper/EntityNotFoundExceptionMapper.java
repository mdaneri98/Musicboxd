package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.models.dtos.ErrorResponseDTO;
import ar.edu.itba.paw.api.exception.ErrorResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.services.exception.not_found.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper para EntityNotFoundException y todas sus subclases.
 * Maneja excepciones de recursos no encontrados (404).
 *
 * Excepciones manejadas:
 * - UserNotFoundException
 * - ArtistNotFoundException
 * - AlbumNotFoundException
 * - SongNotFoundException
 * - ReviewNotFoundException
 * - CommentNotFoundException
 * - NotificationNotFoundException
 * - ImageNotFoundException
 */
@Provider
@Component
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityNotFoundExceptionMapper.class);
    private static final String ENTITY_NOT_FOUND_PACKAGE = "ar.edu.itba.paw.services.exception.not_found";

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        // Solo procesar si es del paquete not_found
        if (!exception.getClass().getName().startsWith(ENTITY_NOT_FOUND_PACKAGE)) {
            // No es nuestra excepción, dejar que otro mapper la maneje
            return null;
        }

        LOGGER.warn("Entity not found: {}", exception.getMessage());

        ErrorResponseDTO error = errorResponseBuilder.buildFromException(
                HttpStatus.NOT_FOUND,
                exception,
                "Entity not found"
        );

        return Response.status(Response.Status.NOT_FOUND)
                .entity(error)
                .build();
    }
}

