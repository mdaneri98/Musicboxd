package ar.edu.itba.paw.api.exception.mapper;

import ar.edu.itba.paw.api.exception.ErrorResponseBuilder;
import ar.edu.itba.paw.api.dto.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationExceptionMapper.class);

	@Autowired
	private ErrorResponseBuilder errorResponseBuilder;

	@Context
	private UriInfo uriInfo;

	@Override
	public Response toResponse(AuthenticationException exception) {
		ErrorResponseDTO error = errorResponseBuilder.buildFromException(
				HttpStatus.UNAUTHORIZED,
				exception,
				"exception.AuthenticationException",
				uriInfo
		);

        LOGGER.error("Email sending failed: {}", error, exception);

		return Response.status(Response.Status.UNAUTHORIZED)
				.header("WWW-Authenticate", "Basic realm=\"API\", Bearer realm=\"API\"")
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(error)
				.build();
	}
}


