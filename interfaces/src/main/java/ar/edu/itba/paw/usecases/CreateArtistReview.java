package ar.edu.itba.paw.usecases;

import ar.edu.itba.paw.models.reviews.Review;

public interface CreateArtistReview {

    Review execute(CreateArtistReviewCommand command);

    record CreateArtistReviewCommand(
        Long userId,
        Long artistId,
        String title,
        String description,
        Integer rating
    ) {
        public CreateArtistReviewCommand {
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("User ID must be positive");
            }
            if (artistId == null || artistId <= 0) {
                throw new IllegalArgumentException("Artist ID must be positive");
            }
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("Title cannot be blank");
            }
            if (title.length() > 50) {
                throw new IllegalArgumentException("Title max length is 50 characters");
            }
            if (description != null && description.length() > 300) {
                throw new IllegalArgumentException("Description max length is 300 characters");
            }
            if (rating == null || rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
        }
    }
}
