package ar.edu.itba.paw.webapp.mapper.resource;

import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.models.resources.ReviewResource;
import ar.edu.itba.paw.webapp.models.links.managers.ReviewLinkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewResourceMapper implements ResourceMapper<ReviewDTO, ReviewResource> {

    @Autowired
    private ReviewLinkManager reviewLinkManager;

    @Override
    public ReviewResource toResource(ReviewDTO reviewDTO, String baseUrl) {
        ReviewResource resource = new ReviewResource(reviewDTO);
        reviewLinkManager.addReviewLinks(resource, baseUrl, reviewDTO.getId());
        return resource;
    }

    @Override
    public List<ReviewResource> toResourceList(List<ReviewDTO> reviewDTOs, String baseUrl) {
        return reviewDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}
