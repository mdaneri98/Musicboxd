package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.models.dtos.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.api.exception.ErrorResponseBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
public class EmailExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailExceptionMapper.class);
    private static final String VERIFICATION_EMAIL = "ar.edu.itba.paw.services.exception.VerificationEmailException";
    private static final String ACKNOWLEDGEMENT_EMAIL = "ar.edu.itba.paw.services.exception.AcknowledgementEmailException";

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Override
    public Response toResponse(RuntimeException exception) {
        String exceptionName = exception.getClass().getName();

        // Solo procesar excepciones de email
        if (!exceptionName.equals(VERIFICATION_EMAIL) &&
                !exceptionName.equals(ACKNOWLEDGEMENT_EMAIL)) {
            return null;
        }

        // Log con nivel ERROR porque es un problema del servidor
        LOGGER.error("Email sending failed: {}", exception.getMessage(), exception);

        ErrorResponseDTO error = errorResponseBuilder.build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorMessages.EMAIL_ERROR
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}

