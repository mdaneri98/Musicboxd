package ar.edu.itba.paw.usecases.user;

public interface UnfollowUser {
    void execute(Long userId, Long targetUserId);
}
