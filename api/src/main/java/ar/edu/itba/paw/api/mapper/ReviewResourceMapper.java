package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.api.models.links.managers.ReviewLinkManager;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
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
