package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.models.dtos.ErrorResponseDTO;
import ar.edu.itba.paw.api.exception.ErrorResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para ConstraintViolationException.
 * Maneja errores de validación Bean Validation (400).
 *
 * Convierte las violaciones de constraints en una lista estructurada
 * de errores de validación por campo.
 */
@Provider
@Component
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationExceptionMapper.class);

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        LOGGER.warn("Validation failed: {} violations", exception.getConstraintViolations().size());

        List<ErrorResponseDTO.ValidationErrorDTO> validationErrors = exception
                .getConstraintViolations()
                .stream()
                .map(this::buildValidationError)
                .collect(Collectors.toList());

        ErrorResponseDTO error = errorResponseBuilder.buildWithValidations(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                validationErrors
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }

    /**
     * Construye un ValidationErrorDTO desde un ConstraintViolation.
     */
    private ErrorResponseDTO.ValidationErrorDTO buildValidationError(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();
        Object rejectedValue = violation.getInvalidValue();

        return new ErrorResponseDTO.ValidationErrorDTO(field, message, rejectedValue);
    }
}

