/**
 * Custom media types matching the backend's CustomMediaType.java
 * Used for content negotiation with vendor-specific media types.
 */

export const CustomMediaType = {
    // User
    USER: 'application/vnd.musicboxd.user.v1+json',
    USER_LIST: 'application/vnd.musicboxd.userList.v1+json',
    USER_PUBLIC: 'application/vnd.musicboxd.user.public.v1+json',
    USER_PASSWORD: 'application/vnd.musicboxd.user.password.v1+json',
    USER_VERIFICATION: 'application/vnd.musicboxd.user.verification.v1+json',

    // Artist
    ARTIST: 'application/vnd.musicboxd.artist.v1+json',
    ARTIST_LIST: 'application/vnd.musicboxd.artistList.v1+json',
    ARTIST_PUBLIC: 'application/vnd.musicboxd.artist.public.v1+json',

    // Album
    ALBUM: 'application/vnd.musicboxd.album.v1+json',
    ALBUM_LIST: 'application/vnd.musicboxd.albumList.v1+json',
    ALBUM_PUBLIC: 'application/vnd.musicboxd.album.public.v1+json',

    // Song
    SONG: 'application/vnd.musicboxd.song.v1+json',
    SONG_LIST: 'application/vnd.musicboxd.songList.v1+json',
    SONG_PUBLIC: 'application/vnd.musicboxd.song.public.v1+json',

    // Review
    REVIEW: 'application/vnd.musicboxd.review.v1+json',
    REVIEW_LIST: 'application/vnd.musicboxd.reviewList.v1+json',

    // Comment
    COMMENT: 'application/vnd.musicboxd.comment.v1+json',
    COMMENT_LIST: 'application/vnd.musicboxd.commentList.v1+json',

    // Notification
    NOTIFICATION: 'application/vnd.musicboxd.notification.v1+json',
    NOTIFICATION_LIST: 'application/vnd.musicboxd.notificationList.v1+json',

    // Other
    IMAGE: 'application/vnd.musicboxd.image.v1+json',
    API_INFO: 'application/vnd.musicboxd.apiInfo.v1+json',
    ERROR: 'application/vnd.musicboxd.error.v1+json',
} as const;
