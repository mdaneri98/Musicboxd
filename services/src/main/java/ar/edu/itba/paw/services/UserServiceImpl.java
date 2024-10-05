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
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
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
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public void updateUserReviewAmount(Long userId) {
        userDao.updateUserReviewAmount(userId);
    }

    @Override
    public List<User> findByUsernameContaining(String sub) {
        return userDao.findByUsernameContaining(sub);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public int create(String username, String email, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        /* Caso que el usuario se haya registrado anteriormente sin datos de usuario, y unicamente con email. */
        Optional<User> emailOptUser = this.findByEmail(email);
        Optional<User> usernameOptUser = this.findByUsername(username);

        if (emailOptUser.isPresent()) {
            if (emailOptUser.get().getUsername() == null){
                User user = emailOptUser.get();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                userDao.update(user);
            }else throw new UserAlreadyExistsException("El correo " + email + " ya está en uso.");
        }
        if (usernameOptUser.isPresent()) {
            throw new UserAlreadyExistsException("El usuario " + username + " ya está en uso.");
        }
        long imgId = imageService.save(null, true);
        int rowsChanged = userDao.create(username, email, hashedPassword, imgId);
        if (rowsChanged > 0) {
            User createdUser = userDao.findByEmail(email).get();
            this.createVerification(VerificationType.VERIFY_EMAIL, createdUser);
        }
        return rowsChanged;
    }

    public UserFollowingData getFollowingData(Long userId, int limit, int offset) {
        if (userId == null || userDao.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("Doesn't exists a user id with value %d".formatted(userId));
        }
        List<User> followers = userDao.getFollowers(userId, limit, offset);
        List<User> following = userDao.getFollowing(userId, limit, offset);
        return new UserFollowingData(followers, following);
    }

    @Override
    public boolean isFollowing(Long userId, Long otherId) {
        return userDao.isFollowing(userId, otherId);
    }

    @Override
    public boolean isAlbumFavorite(Long userId, Long albumId) {
        return userDao.isAlbumFavorite(userId, albumId);
    }

    @Override
    public boolean isArtistFavorite(Long userId, Long artistId) {
        return userDao.isArtistFavorite(userId, artistId);
    }

    @Override
    public boolean isSongFavorite(Long userId, Long songId) {
        return userDao.isSongFavorite(userId, songId);
    }

    @Override
    public int createFollowing(User userId, long followingId) {
        //FIXME: Corregir que sea tipo 'Long userId'
        if (this.isFollowing(userId.getId(), followingId)) {
            return 0;
        }
        return userDao.createFollowing(userId, findById(followingId).get());
    }

    @Override
    public int undoFollowing(User userId, long followingId) {
        return userDao.undoFollowing(userId, findById(followingId).get());
    }

    @Override
    public void createVerification(VerificationType type, User user) {
        try {
            String verificationCode = UUID.randomUUID().toString();

            // Codifica el código de verificación para asegurarte de que sea seguro para la URL
            String encodedVerificationCode = URLEncoder.encode(verificationCode, StandardCharsets.UTF_8);

            userVerificationDao.startVerification(type, user, encodedVerificationCode);

            emailService.sendVerification(type, user.getEmail(), encodedVerificationCode);

        } catch (MessagingException e) {
            logger.error("Error al enviar el correo de verificación al usuario: {}", user.getEmail(), e);
            throw new VerificationEmailException("No se pudo enviar la verificación del email al usuario " + user.getEmail(), e);
        }
    }

    @Override
    public Long verify(VerificationType type, String code) {
        return userVerificationDao.verify(type, code);
    }

    @Override
    public int update(User user) {
        return userDao.update(user);
    }

    @Override
    public boolean changePassword(Long userId, String newPassword) {
        return userDao.changePassword(userId, passwordEncoder.encode(newPassword));
    }

    @Override
    public int update(User user, byte[] bytes) {
        long imgId = imageService.update(user.getImgId(), bytes);
        user.setImgId(imgId);
        return userDao.update(user);
    }

    @Override
    public int deleteById(long id) {
        return userDao.deleteById(id);
    }

    @Override
    public List<Artist> getFavoriteArtists(long userId) {
        return userDao.getFavoriteArtists(userId);
    }

    @Override
    public boolean addFavoriteArtist(long userId, long artistId) {
        if (getFavoriteArtistsCount(userId) >= 5) {
            return false;
        }
        return userDao.addFavoriteArtist(userId, artistId);
    }

    @Override
    public boolean removeFavoriteArtist(long userId, long artistId) {
        return userDao.removeFavoriteArtist(userId, artistId);
    }

    @Override
    public int getFavoriteArtistsCount(long userId) {
        return userDao.getFavoriteArtistsCount(userId);
    }

    @Override
    public List<Album> getFavoriteAlbums(long userId) {
        return userDao.getFavoriteAlbums(userId);
    }

    @Override
    public boolean addFavoriteAlbum(long userId, long albumId) {
        if (getFavoriteAlbumsCount(userId) >= 5) {
            return false;
        }
        return userDao.addFavoriteAlbum(userId, albumId);
    }

    @Override
    public boolean removeFavoriteAlbum(long userId, long albumId) {
        return userDao.removeFavoriteAlbum(userId, albumId);
    }

    @Override
    public int getFavoriteAlbumsCount(long userId) {
        return userDao.getFavoriteAlbumsCount(userId);
    }

    @Override
    public List<Song> getFavoriteSongs(long userId) {
        return userDao.getFavoriteSongs(userId);
    }

    @Override
    public boolean addFavoriteSong(long userId, long songId) {
        if (getFavoriteSongsCount(userId) >= 5) {
            return false;
        }
        return userDao.addFavoriteSong(userId, songId);
    }

    @Override
    public boolean removeFavoriteSong(long userId, long songId) {
        return userDao.removeFavoriteSong(userId, songId);
    }

    @Override
    public int getFavoriteSongsCount(long userId) {
        return userDao.getFavoriteSongsCount(userId);
    }
}