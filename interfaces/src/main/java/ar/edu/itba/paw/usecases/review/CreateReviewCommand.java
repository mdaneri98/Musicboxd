package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ReviewType;

public record CreateReviewCommand(
    Long userId,
    String title,
    String description,
    Integer rating,
    ReviewType reviewType,
    Long itemId
) {}
