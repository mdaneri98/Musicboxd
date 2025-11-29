package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.api.dto.ErrorResponseDTO;
import ar.edu.itba.paw.api.exception.ErrorResponseBuilder;
import ar.edu.itba.paw.exception.conflict.ConflictException; 
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
 * Mapper para ConflictException y todas sus subclases.
 * Maneja excepciones de conflicto de recursos (409).
 *
 * Excepciones manejadas:
 * - UserAlreadyExistsException
 * - UserHasAlreadyReviewedException
 * - Otras excepciones de conflicto que extiendan ConflictException
 */
@Provider
@Component
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConflictExceptionMapper.class);

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Override 
    public Response toResponse(ConflictException exception) {
        LOGGER.warn("Conflict exception: {}", exception.getMessage());

        ErrorResponseDTO error = errorResponseBuilder.buildFromException(
                HttpStatus.CONFLICT,
                exception,
                "exception.BadRequest",
                uriInfo
        );

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}

