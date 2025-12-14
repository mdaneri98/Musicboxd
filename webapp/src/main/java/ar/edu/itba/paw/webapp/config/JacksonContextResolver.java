package ar.edu.itba.paw.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * JAX-RS ContextResolver to integrate Spring's ObjectMapper with Jersey.
 * 
 * This resolver ensures that Jersey uses the custom ObjectMapper configured in JacksonConfig,
 * which includes:
 * - snake_case naming strategy (bidirectional)
 * - Null value exclusion
 * - Java 8 Date/Time support
 * - ISO-8601 date formatting
 * 
 * Without this resolver, Jersey would use its own default ObjectMapper,
 * ignoring all Spring configuration.
 */
@Provider
@Component
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {
    
    private final ObjectMapper objectMapper;
    
    @Autowired
    public JacksonContextResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}

