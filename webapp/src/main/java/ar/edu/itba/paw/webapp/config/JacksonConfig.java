package ar.edu.itba.paw.webapp.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson configuration for JSON serialization/deserialization.
 * 
 * Features:
 * - Output (Serialization): snake_case format
 * - Input (Deserialization): Accepts snake_case and converts to camelCase
 * - Null values are excluded from JSON output
 * - Java 8 Date/Time API support (LocalDateTime, etc.)
 * - Dates formatted as ISO-8601 strings instead of timestamps
 * - Unknown properties are ignored during deserialization
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // ============ Date/Time Configuration ============
        // Register Java 8 Date/Time module for LocalDateTime, LocalDate, etc.
        mapper.registerModule(new JavaTimeModule());
        
        // Serialize dates as ISO-8601 strings (e.g., "2024-10-13T15:30:00") 
        // instead of timestamps (e.g., 1697207400000)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // ============ Naming Strategy ============
        // Use snake_case for both serialization (output) and deserialization (input)
        // This ensures bidirectional conversion:
        // - Java camelCase fields → JSON snake_case
        // - JSON snake_case → Java camelCase fields
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        // ============ Null Handling ============
        // Exclude null fields from JSON output (cleaner responses)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // ============ Deserialization Settings ============
        // Ignore unknown properties in JSON input (more flexible API)
        // This prevents errors if the client sends extra fields
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Accept single values as arrays (e.g., "tags": "java" → "tags": ["java"])
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        
        // ============ Serialization Settings ============
        // Pretty print JSON in development (can be disabled in production)
        // mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        return mapper;
    }
}
