package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.api.dto.ErrorResponseDTO;
import ar.edu.itba.paw.api.exception.ErrorResponseBuilder;
import ar.edu.itba.paw.exception.not_found.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.MediaType;
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

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        ErrorResponseDTO error = errorResponseBuilder.buildFromException(
                HttpStatus.NOT_FOUND,
                exception,
                "exception.EntityNotFound",
                uriInfo
        );

        LOGGER.warn("Entity not found: {}", error.getMessage());

        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}

