package ar.edu.itba.paw.persistence.mappers;

import ar.edu.itba.paw.domain.user.Email;
import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.reconstitute(
            new UserId(entity.getId()),
            entity.getUsername(),
            new Email(entity.getEmail()),
            entity.getPassword(),
            entity.getVerified(),
            entity.getIsModerator(),
            entity.getReviewAmount(),
            entity.getFollowerAmount(),
            entity.getFollowingAmount(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getName(),
            entity.getBio(),
            entity.getImageId(),
            entity.getPreferredLanguage() != null ? entity.getPreferredLanguage() : "en",
            entity.getTheme() != null ? entity.getTheme() : "light",
            entity.getFollowNotificationsEnabled() != null ? entity.getFollowNotificationsEnabled() : true,
            entity.getLikeNotificationsEnabled() != null ? entity.getLikeNotificationsEnabled() : true,
            entity.getCommentNotificationsEnabled() != null ? entity.getCommentNotificationsEnabled() : true,
            entity.getReviewNotificationsEnabled() != null ? entity.getReviewNotificationsEnabled() : true
        );
    }

    public UserJpaEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        UserJpaEntity entity = new UserJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail().getValue());
        entity.setPassword(domain.getPassword());
        entity.setVerified(domain.isVerified());
        entity.setIsModerator(domain.isModerator());
        entity.setReviewAmount(domain.getReviewAmount());
        entity.setFollowerAmount(domain.getFollowerAmount());
        entity.setFollowingAmount(domain.getFollowingAmount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setName(domain.getName());
        entity.setBio(domain.getBio());
        entity.setImageId(domain.getImageId());
        entity.setPreferredLanguage(domain.getPreferredLanguage());
        entity.setTheme(domain.getTheme());
        entity.setFollowNotificationsEnabled(domain.getFollowNotificationsEnabled());
        entity.setLikeNotificationsEnabled(domain.getLikeNotificationsEnabled());
        entity.setCommentNotificationsEnabled(domain.getCommentNotificationsEnabled());
        entity.setReviewNotificationsEnabled(domain.getReviewNotificationsEnabled());

        return entity;
    }
}
