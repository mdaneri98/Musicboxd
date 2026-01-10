package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.ApiInfoDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * HATEOAS resource wrapper for API root information
 */
public class ApiInfoResource extends Resource<ApiInfoDTO> {

    @JsonUnwrapped
    private final ApiInfoDTO apiInfo;

    public ApiInfoResource(ApiInfoDTO apiInfo) {
        this.apiInfo = apiInfo;
    }
}
