package ar.edu.itba.paw.usecases.user;

public interface UpdateUserReviewCount {
    void decrement(Long userId);
    void increment(Long userId);
}
