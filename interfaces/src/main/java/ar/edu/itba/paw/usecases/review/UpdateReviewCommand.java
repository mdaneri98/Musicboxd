package ar.edu.itba.paw.usecases.review;

public record UpdateReviewCommand(
    Long reviewId,
    String title,
    String description,
    Integer rating
) {}
