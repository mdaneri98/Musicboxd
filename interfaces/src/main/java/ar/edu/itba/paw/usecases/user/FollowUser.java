package ar.edu.itba.paw.usecases.user;

public interface FollowUser {
    void execute(Long userId, Long targetUserId);
}
