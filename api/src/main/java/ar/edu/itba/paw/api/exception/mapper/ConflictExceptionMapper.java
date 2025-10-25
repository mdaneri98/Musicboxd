package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.models.dtos.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.api.exception.ErrorResponseBuilder;
import ar.edu.itba.paw.services.exception.conflict.ConflictException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
    private static final String CONFLICT_EXCEPTION_NAME = "ar.edu.itba.paw.services.exception.conflict.ConflictException";
    private static final String USER_ALREADY_EXISTS = "ar.edu.itba.paw.services.exception.conflict.UserAlreadyExistsException";
    private static final String USER_HAS_ALREADY_REVIEWED = "ar.edu.itba.paw.services.exception.conflict.UserHasAlreadyReviewedException";

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Override
    public Response toResponse(ConflictException exception) {
        String exceptionName = exception.getClass().getName();

        // Solo procesar si es ConflictException o sus subclases
        if (!exceptionName.equals(CONFLICT_EXCEPTION_NAME) &&
                !exceptionName.equals(USER_ALREADY_EXISTS) &&
                !exceptionName.equals(USER_HAS_ALREADY_REVIEWED)) {
            return null;
        }

        LOGGER.warn("Conflict exception: {}", exception.getMessage());

        ErrorResponseDTO error = errorResponseBuilder.buildFromException(
                HttpStatus.CONFLICT,
                exception,
                "Resource already exists"
        );

        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .build();
    }
}

