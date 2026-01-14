package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Utility class for adding RFC 5988 compliant pagination links to JAX-RS responses.
 * Uses JAX-RS native link() method for standard HTTP Link headers.
 *
 * https://www.baeldung.com/rest-api-pagination-in-spring
 */
public final class PaginationHeadersBuilder {

    private PaginationHeadersBuilder() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static Response.ResponseBuilder addPaginationHeaders(
            Response.ResponseBuilder responseBuilder,
            UriInfo uriInfo,
            Integer currentPage,
            Integer pageSize,
            Long totalCount) {

        if (totalCount == null || pageSize == null || currentPage == null || pageSize <= 0) {
            return responseBuilder;
        }

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        responseBuilder.link(
            uriInfo.getRequestUriBuilder()
                .replaceQueryParam("page", 1)
                .replaceQueryParam("size", pageSize)
                .build()
                .toString(),
            "first"
        );

        if (currentPage > 1) {
            responseBuilder.link(
                uriInfo.getRequestUriBuilder()
                    .replaceQueryParam("page", currentPage - 1)
                    .replaceQueryParam("size", pageSize)
                    .build()
                    .toString(),
                "prev"
            );
        }

        if (currentPage < totalPages) {
            responseBuilder.link(
                uriInfo.getRequestUriBuilder()
                    .replaceQueryParam("page", currentPage + 1)
                    .replaceQueryParam("size", pageSize)
                    .build()
                    .toString(),
                "next"
            );
        }

        responseBuilder.link(
            uriInfo.getRequestUriBuilder()
                .replaceQueryParam("page", totalPages)
                .replaceQueryParam("size", pageSize)
                .build()
                .toString(),
            "last"
        );

        return responseBuilder;
    }
}
