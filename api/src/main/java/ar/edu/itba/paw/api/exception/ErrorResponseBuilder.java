package ar.edu.itba.paw.api.exception;

import ar.edu.itba.paw.models.dtos.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Builder para construir respuestas de error estandarizadas.
 * Centraliza la lógica de construcción de ErrorResponseDTO siguiendo el principio SRP.
 * 
 * Nota: UriInfo se pasa como parámetro en lugar de inyectarse con @Context
 * porque @Context solo funciona en clases @Provider de JAX-RS, no en @Component de Spring.
 */
@Component
public class ErrorResponseBuilder {

    /**
     * Construye una respuesta de error básica.
     *
     * @param httpStatus Estado HTTP
     * @param message Mensaje de error
     * @param uriInfo Información de la URI del request (puede ser null)
     * @return ErrorResponseDTO construido
     */
    public ErrorResponseDTO build(HttpStatus httpStatus, String message, UriInfo uriInfo) {
        return new ErrorResponseDTO(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                getPath(uriInfo)
        );
    }

    /**
     * Construye una respuesta de error con código y estado personalizados.
     *
     * @param code Código HTTP
     * @param status Estado HTTP como string
     * @param message Mensaje de error
     * @param uriInfo Información de la URI del request (puede ser null)
     * @return ErrorResponseDTO construido
     */
    public ErrorResponseDTO build(int code, String status, String message, UriInfo uriInfo) {
        return new ErrorResponseDTO(
                code,
                status,
                message,
                getPath(uriInfo)
        );
    }

    /**
     * Construye una respuesta de error con validaciones.
     *
     * @param httpStatus Estado HTTP
     * @param message Mensaje de error
     * @param validationErrors Lista de errores de validación
     * @param uriInfo Información de la URI del request (puede ser null)
     * @return ErrorResponseDTO construido
     */
    public ErrorResponseDTO buildWithValidations(
            HttpStatus httpStatus,
            String message,
            List<ErrorResponseDTO.ValidationErrorDTO> validationErrors,
            UriInfo uriInfo) {
        return new ErrorResponseDTO(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                getPath(uriInfo),
                validationErrors
        );
    }

    /**
     * Construye una respuesta de error desde una excepción con mensaje por defecto.
     *
     * @param httpStatus Estado HTTP
     * @param exception Excepción lanzada
     * @param defaultMessage Mensaje por defecto si la excepción no tiene mensaje
     * @param uriInfo Información de la URI del request (puede ser null)
     * @return ErrorResponseDTO construido
     */
    public ErrorResponseDTO buildFromException(
            HttpStatus httpStatus,
            Throwable exception,
            String defaultMessage,
            UriInfo uriInfo) {
        String message = exception.getMessage() != null ? exception.getMessage() : defaultMessage;
        return build(httpStatus, message, uriInfo);
    }

    /**
     * Extrae la ruta del request desde UriInfo.
     *
     * @param uriInfo Información de la URI del request (puede ser null)
     * @return Path del request o "unknown" si no está disponible
     */
    private String getPath(UriInfo uriInfo) {
        return uriInfo != null ? uriInfo.getBaseUri().toString() + uriInfo.getPath() : "unknown";
    }
}

