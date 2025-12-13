package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.api.dto.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.api.exception.ErrorResponseBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import ar.edu.itba.paw.exception.email.EmailException;
import javax.ws.rs.core.MediaType;
/**
 * Mapper para excepciones relacionadas con el envío de emails (500).
 * Maneja errores que ocurren al intentar enviar correos electrónicos.
 *
 * Excepciones manejadas:
 * - VerificationEmailException
 * - AcknowledgementEmailException
 */
@Provider
@Component
public class EmailExceptionMapper implements ExceptionMapper<EmailException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailExceptionMapper.class);

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Override
    public Response toResponse(EmailException exception) {
        ErrorResponseDTO error = errorResponseBuilder.buildFromException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception,
                "exception.InternalServerError",
                uriInfo
        );

        LOGGER.error("Email sending failed: {}", error, exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}

