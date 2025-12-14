package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

@Provider
@Component
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	@Override
	public Response toResponse(WebApplicationException exception) {
        return Response.status(exception.getResponse().getStatus())
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(exception.getMessage())
				.build();
	}
}


