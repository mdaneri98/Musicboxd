package ar.edu.itba.paw.webapp.exception.mapper;

import ar.edu.itba.paw.webapp.exception.ErrorResponseBuilder;
import ar.edu.itba.paw.webapp.dto.ErrorResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

	@Autowired
	private ErrorResponseBuilder errorResponseBuilder;

	@Context
	private UriInfo uriInfo;

	@Override
	public Response toResponse(AccessDeniedException exception) {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final boolean authenticated = auth != null && auth.isAuthenticated();

		final Response.Status status = authenticated ? Response.Status.FORBIDDEN : Response.Status.UNAUTHORIZED;
		final HttpStatus springStatus = authenticated ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;

		ErrorResponseDTO error = errorResponseBuilder.buildFromException(
				springStatus,
				exception,
				authenticated ? "exception.AccessDeniedException" : "exception.AuthenticationException",
				uriInfo);

		Response.ResponseBuilder builder = Response.status(status)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(error);

		if (!authenticated) {
			builder.header("WWW-Authenticate", "Basic realm=\"API\", Bearer realm=\"API\"");
		}

		return builder.build();
	}
}
