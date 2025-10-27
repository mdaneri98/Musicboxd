package ar.edu.itba.paw.api.exception;

import ar.edu.itba.paw.models.dtos.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Builder para construir respuestas de error estandarizadas.
 * Centraliza la lógica de construcción de ErrorResponseDTO siguiendo el principio SRP.
 */
@Component
public class ErrorResponseBuilder {

    @Context
    private UriInfo uriInfo;

    /**
     * Construye una respuesta de error básica.
     *
     * @param httpStatus Estado HTTP
     * @param message Mensaje de error
     * @return ErrorResponseDTO construido
     */
    public ErrorResponseDTO build(HttpStatus httpStatus, String message) {
        return new ErrorResponseDTO(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                getPath()
        );
    }

    /**
     * Construye una respuesta de error con código y estado personalizados.
     *
     * @param code Código HTTP
     * @param status Estado HTTP como string
     * @param message Mensaje de error
     * @return ErrorResponseDTO construido
     */
    public ErrorResponseDTO build(int code, String status, String message) {
        return new ErrorResponseDTO(
                code,
                status,
                message,
                getPath()
        );
    }

    /**
     * Construye una respuesta de error con validaciones.
     *
     * @param httpStatus Estado HTTP
     * @param message Mensaje de error
     * @param validationErrors Lista de errores de validación
     * @return ErrorResponseDTO construido
     */
    public ErrorResponseDTO buildWithValidations(
            HttpStatus httpStatus,
            String message,
            List<ErrorResponseDTO.ValidationErrorDTO> validationErrors) {
        return new ErrorResponseDTO(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                getPath(),
                validationErrors
        );
    }

    /**
     * Construye una respuesta de error desde una excepción con mensaje por defecto.
     *
     * @param httpStatus Estado HTTP
     * @param exception Excepción lanzada
     * @param defaultMessage Mensaje por defecto si la excepción no tiene mensaje
     * @return ErrorResponseDTO construido
     */
    public ErrorResponseDTO buildFromException(
            HttpStatus httpStatus,
            Throwable exception,
            String defaultMessage) {
        String message = exception.getMessage() != null ? exception.getMessage() : defaultMessage;
        return build(httpStatus, message);
    }

    /**
     * Obtiene la ruta actual del request.
     *
     * @return Path del request o "unknown" si no está disponible
     */
    private String getPath() {
        return uriInfo != null ? uriInfo.getPath() : "unknown";
    }
}

