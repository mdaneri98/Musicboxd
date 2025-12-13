package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.api.dto.ErrorResponseDTO;
import ar.edu.itba.paw.api.exception.ErrorResponseBuilder;
import ar.edu.itba.paw.exception.UnkownReviewTypeException;
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
 * Mapper para excepciones de Bad Request (400).
 * Maneja errores de solicitudes mal formadas o inválidas.
 *
 * Excepciones manejadas:
 * - UnkownReviewTypeException
 */
@Provider
@Component
public class BadRequestExceptionMapper implements ExceptionMapper<UnkownReviewTypeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BadRequestExceptionMapper.class);

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Override
    public Response toResponse(UnkownReviewTypeException exception) {
        ErrorResponseDTO error = errorResponseBuilder.buildFromException(
                HttpStatus.BAD_REQUEST,
                exception,
                "exception.BadRequest",
                uriInfo
        );

        LOGGER.error("Email sending failed: {}", error, exception);

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}

