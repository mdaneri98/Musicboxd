package ar.edu.itba.paw.usecases.user;

public class UpdateUserConfigCommand {
    private final Long userId;
    private final String preferredLanguage;
    private final String theme;
    private final Boolean followNotificationsEnabled;
    private final Boolean likeNotificationsEnabled;
    private final Boolean commentNotificationsEnabled;
    private final Boolean reviewNotificationsEnabled;

    public UpdateUserConfigCommand(Long userId, String preferredLanguage, String theme,
                                   Boolean followNotificationsEnabled, Boolean likeNotificationsEnabled,
                                   Boolean commentNotificationsEnabled, Boolean reviewNotificationsEnabled) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID required");
        }
        this.userId = userId;
        this.preferredLanguage = preferredLanguage;
        this.theme = theme;
        this.followNotificationsEnabled = followNotificationsEnabled;
        this.likeNotificationsEnabled = likeNotificationsEnabled;
        this.commentNotificationsEnabled = commentNotificationsEnabled;
        this.reviewNotificationsEnabled = reviewNotificationsEnabled;
    }

    public Long userId() {
        return userId;
    }

    public String preferredLanguage() {
        return preferredLanguage;
    }

    public String theme() {
        return theme;
    }

    public Boolean followNotificationsEnabled() {
        return followNotificationsEnabled;
    }

    public Boolean likeNotificationsEnabled() {
        return likeNotificationsEnabled;
    }

    public Boolean commentNotificationsEnabled() {
        return commentNotificationsEnabled;
    }

    public Boolean reviewNotificationsEnabled() {
        return reviewNotificationsEnabled;
    }
}
