package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.models.dtos.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper para excepciones de Bad Request (400).
 * Maneja errores de solicitudes mal formadas o inválidas.
 *
 * Excepciones manejadas:
 * - UnkownReviewTypeException
 * - Otras excepciones de tipo BadRequest
 */
@Provider
@Component
public class BadRequestExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BadRequestExceptionMapper.class);
    private static final String UNKNOWN_REVIEW_TYPE = "ar.edu.itba.paw.services.exception.UnkownReviewTypeException";

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Override
    public Response toResponse(RuntimeException exception) {
        String exceptionName = exception.getClass().getName();

        // Solo procesar UnkownReviewTypeException
        if (!exceptionName.equals(UNKNOWN_REVIEW_TYPE)) {
            return null;
        }

        LOGGER.warn("Bad request: {}", exception.getMessage());

        ErrorResponseDTO error = errorResponseBuilder.buildFromException(
                HttpStatus.BAD_REQUEST,
                exception,
                ErrorMessages.BAD_REQUEST
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }
}

