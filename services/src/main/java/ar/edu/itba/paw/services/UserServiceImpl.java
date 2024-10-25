package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.persistence.UserVerificationDao;
import ar.edu.itba.paw.services.exception.UserAlreadyExistsException;
import ar.edu.itba.paw.services.exception.VerificationEmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;
    private final UserVerificationDao userVerificationDao;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    private final ImageService imageService;

    public UserServiceImpl(UserDao userDao, UserVerificationDao userVerificationDao, PasswordEncoder passwordEncoder, EmailService emailService, ImageService imageService) {
        this.userDao = userDao;
        this.userVerificationDao = userVerificationDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.imageService = imageService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> find(long id) {
        return userDao.find(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public boolean usernameExists(String username) {
        return userDao.findByUsername(username).isPresent();
    }

    @Override
    @Transactional
    public void updateUserReviewAmount(Long userId) {
        LOGGER.info("Updating review amount for user with ID: {}", userId);
        userDao.updateUserReviewAmount(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByUsernameContaining(String sub) {
        return userDao.findByUsernameContaining(sub);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    @Transactional
    public Optional<User> create(String username, String email, String password) {
        LOGGER.info("Creating new user with username: {} and email: {}", username, email);
        String hashedPassword = passwordEncoder.encode(password);

        Optional<User> emailOptUser = this.findByEmail(email);
        Optional<User> usernameOptUser = this.findByUsername(username);

        if (emailOptUser.isPresent()) {
            if (emailOptUser.get().getUsername() == null){
                User user = emailOptUser.get();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                userDao.update(user);
                LOGGER.info("Updated existing user with email: {}", email);
            } else {
                LOGGER.warn("Attempt to create user with existing email: {}", email);
                throw new UserAlreadyExistsException("El correo " + email + " ya está en uso.");
            }
        }
        if (usernameOptUser.isPresent()) {
            LOGGER.warn("Attempt to create user with existing username: {}", username);
            throw new UserAlreadyExistsException("El usuario " + username + " ya está en uso.");
        }
        long imgId = imageService.save(null, true);
        Optional<User> userOpt = userDao.create(username, email, hashedPassword, imgId);
        if (userOpt.isPresent()) {
            User createdUser = userOpt.get();
            this.createVerification(VerificationType.VERIFY_EMAIL, createdUser);
            LOGGER.info("Successfully created new user with ID: {}", createdUser.getId());
        } else {
            LOGGER.error("Failed to create new user with username: {} and email: {}", username, email);
        }
        return userOpt;
    }

    @Override
    @Transactional(readOnly = true)
    public UserFollowingData getFollowingData(Long userId, int limit, int offset) {
        if (userId == null || userDao.find(userId).isEmpty()) {
            throw new IllegalArgumentException("Doesn't exists a user id with value %d".formatted(userId));
        }
        List<User> followers = userDao.getFollowers(userId, limit, offset);
        List<User> following = userDao.getFollowing(userId, limit, offset);
        return new UserFollowingData(followers, following);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long userId, Long otherId) {
        return userDao.isFollowing(userId, otherId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAlbumFavorite(Long userId, Long albumId) {
        return userDao.isAlbumFavorite(userId, albumId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isArtistFavorite(Long userId, Long artistId) {
        return userDao.isArtistFavorite(userId, artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSongFavorite(Long userId, Long songId) {
        return userDao.isSongFavorite(userId, songId);
    }

    @Override
    @Transactional
    public int createFollowing(User userId, long followingId) {
        LOGGER.info("Creating following relationship: User {} following User {}", userId.getId(), followingId);
        if (this.isFollowing(userId.getId(), followingId)) {
            LOGGER.info("Following relationship already exists");
            return 0;
        }
        int result = userDao.createFollowing(userId, find(followingId).get());
        LOGGER.info("Following relationship created successfully");
        return result;
    }

    @Override
    @Transactional
    public int undoFollowing(User userId, long followingId) {
        LOGGER.info("Removing following relationship: User {} unfollowing User {}", userId.getId(), followingId);
        int result = userDao.undoFollowing(userId, find(followingId).get());
        LOGGER.info("Following relationship removed successfully");
        return result;
    }

    @Override
    @Transactional
    public void createVerification(VerificationType type, User user) {
        LOGGER.info("Creating verification for user: {} with type: {}", user.getId(), type);
        try {
            String verificationCode = UUID.randomUUID().toString();
            String encodedVerificationCode = URLEncoder.encode(verificationCode, StandardCharsets.UTF_8);
            userVerificationDao.startVerification(type, user, encodedVerificationCode);

            emailService.sendVerification(type, user.getEmail(), encodedVerificationCode);
            LOGGER.info("Verification created and email sent successfully");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send verification email to user: {}", user.getEmail(), e);
            throw new VerificationEmailException("No se pudo enviar la verificación del email al usuario " + user.getEmail(), e);
        }
    }

    @Override
    @Transactional
    public Long verify(VerificationType type, String code) {
        LOGGER.info("Verifying user with type: {} and code: {}", type, code);
        Long result = userVerificationDao.verify(type, code);
        if (result != null) {
            LOGGER.info("User verified successfully");
        } else {
            LOGGER.warn("User verification failed");
        }
        return result;
    }

    @Override
    @Transactional
    public int update(User user) {
        LOGGER.info("Updating user with ID: {}", user.getId());
        int result = userDao.update(user);
        LOGGER.info("User updated successfully");
        return result;
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String newPassword) {
        LOGGER.info("Changing password for user with ID: {}", userId);
        boolean result = userDao.changePassword(userId, passwordEncoder.encode(newPassword));
        if (result) {
            LOGGER.info("Password changed successfully for user with ID: {}", userId);
        } else {
            LOGGER.warn("Failed to change password for user with ID: {}", userId);
        }
        return result;
    }

    @Override
    @Transactional
    public int update(User user, byte[] bytes) {
        LOGGER.info("Updating user with ID: {} and changing profile image", user.getId());
        long imgId = imageService.update(user.getImgId(), bytes);
        user.setImgId(imgId);
        int result = userDao.update(user);
        LOGGER.info("User updated and profile image changed successfully");
        return result;
    }

    @Override
    @Transactional
    public int deleteById(long id) {
        LOGGER.info("Deleting user with ID: {}", id);
        int result = userDao.deleteById(id);
        if (result > 0) {
            LOGGER.info("User with ID: {} deleted successfully", id);
        } else {
            LOGGER.warn("Failed to delete user with ID: {}", id);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> getFavoriteArtists(long userId) {
        return userDao.getFavoriteArtists(userId);
    }

    @Override
    @Transactional
    public boolean addFavoriteArtist(long userId, long artistId) {
        LOGGER.info("Adding favorite artist with ID: {} for user with ID: {}", artistId, userId);
        if (getFavoriteArtistsCount(userId) >= 5) {
            LOGGER.warn("User with ID: {} has reached the maximum number of favorite artists", userId);
            return false;
        }
        boolean result = userDao.addFavoriteArtist(userId, artistId);
        if (result) {
            LOGGER.info("Favorite artist added successfully");
        } else {
            LOGGER.warn("Failed to add favorite artist");
        }
        return result;
    }

    @Override
    @Transactional
    public boolean removeFavoriteArtist(long userId, long artistId) {
        LOGGER.info("Removing favorite artist with ID: {} for user with ID: {}", artistId, userId);
        boolean result = userDao.removeFavoriteArtist(userId, artistId);
        if (result) {
            LOGGER.info("Favorite artist removed successfully");
        } else {
            LOGGER.warn("Failed to remove favorite artist");
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public int getFavoriteArtistsCount(long userId) {
        return userDao.getFavoriteArtistsCount(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> getFavoriteAlbums(long userId) {
        return userDao.getFavoriteAlbums(userId);
    }

    @Override
    @Transactional
    public boolean addFavoriteAlbum(long userId, long albumId) {
        LOGGER.info("Adding favorite album with ID: {} for user with ID: {}", albumId, userId);
        if (getFavoriteAlbumsCount(userId) >= 5) {
            LOGGER.warn("User with ID: {} has reached the maximum number of favorite albums", userId);
            return false;
        }
        boolean result = userDao.addFavoriteAlbum(userId, albumId);
        if (result) {
            LOGGER.info("Favorite album added successfully");
        } else {
            LOGGER.warn("Failed to add favorite album");
        }
        return result;
    }

    @Override
    @Transactional
    public boolean removeFavoriteAlbum(long userId, long albumId) {
        LOGGER.info("Removing favorite album with ID: {} for user with ID: {}", albumId, userId);
        boolean result = userDao.removeFavoriteAlbum(userId, albumId);
        if (result) {
            LOGGER.info("Favorite album removed successfully");
        } else {
            LOGGER.warn("Failed to remove favorite album");
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public int getFavoriteAlbumsCount(long userId) {
        return userDao.getFavoriteAlbumsCount(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> getFavoriteSongs(long userId) {
        return userDao.getFavoriteSongs(userId);
    }

    @Override
    @Transactional
    public boolean addFavoriteSong(long userId, long songId) {
        LOGGER.info("Adding favorite song with ID: {} for user with ID: {}", songId, userId);
        if (getFavoriteSongsCount(userId) >= 5) {
            LOGGER.warn("User with ID: {} has reached the maximum number of favorite songs", userId);
            return false;
        }
        boolean result = userDao.addFavoriteSong(userId, songId);
        if (result) {
            LOGGER.info("Favorite song added successfully");
        } else {
            LOGGER.warn("Failed to add favorite song");
        }
        return result;
    }

    @Override
    @Transactional
    public boolean removeFavoriteSong(long userId, long songId) {
        LOGGER.info("Removing favorite song with ID: {} for user with ID: {}", songId, userId);
        boolean result = userDao.removeFavoriteSong(userId, songId);
        if (result) {
            LOGGER.info("Favorite song removed successfully");
        } else {
            LOGGER.warn("Failed to remove favorite song");
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public int getFavoriteSongsCount(long userId) {
        return userDao.getFavoriteSongsCount(userId);
    }
}