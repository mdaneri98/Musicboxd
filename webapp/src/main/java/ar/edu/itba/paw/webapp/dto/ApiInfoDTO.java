package ar.edu.itba.paw.webapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for API root information
 */
public class ApiInfoDTO {
    
    @JsonProperty("name")
    private final String name;
    
    @JsonProperty("version")
    private final String version;
    
    @JsonProperty("description")
    private final String description;
    
    public ApiInfoDTO(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getDescription() {
        return description;
    }
}
