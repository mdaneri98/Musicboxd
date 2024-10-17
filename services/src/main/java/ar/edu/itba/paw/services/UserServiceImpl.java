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
            } else {
                throw new UserAlreadyExistsException("El correo " + email + " ya está en uso.");
            }
        }
        if (usernameOptUser.isPresent()) {
            throw new UserAlreadyExistsException("El usuario " + username + " ya está en uso.");
        }
        long imgId = imageService.save(null, true);
        Optional<User> userOpt = userDao.create(username, email, hashedPassword, imgId);
        if (userOpt.isPresent()) {
            User createdUser = userOpt.get();
            this.createVerification(VerificationType.VERIFY_EMAIL, createdUser);
        }
        return userOpt;
    }

//    @Override
//    @Transactional(readOnly = true)
//    public UserFollowingData getFollowingData(Long userId, int limit, int offset) {
//        if (userId == null || userDao.find(userId).isEmpty()) {
//            throw new IllegalArgumentException("Doesn't exists a user id with value %d".formatted(userId));
//        }
//        List<User> followers = userDao.getFollowers(userId, limit, offset);
//        List<User> following = userDao.getFollowing(userId, limit, offset);
//        return new UserFollowingData(userId, followers, following);
//    }

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
        if (this.isFollowing(userId.getId(), followingId)) {
            return 0;
        }
        return userDao.createFollowing(userId, find(followingId).get());
    }

    @Override
    @Transactional
    public int undoFollowing(User userId, long followingId) {
        return userDao.undoFollowing(userId, find(followingId).get());
    }

    @Override
    @Transactional
    public void createVerification(VerificationType type, User user) {
        try {
            String verificationCode = UUID.randomUUID().toString();

            // Codifica el código de verificación para asegurarte de que sea seguro para la URL
            String encodedVerificationCode = URLEncoder.encode(verificationCode, StandardCharsets.UTF_8);

            userVerificationDao.startVerification(type, user, encodedVerificationCode);

            emailService.sendVerification(type, user.getEmail(), encodedVerificationCode);
        } catch (MessagingException e) {
            //logger.error("Error al enviar el correo de verificación al usuario: {}", user.getEmail(), e);
            throw new VerificationEmailException("No se pudo enviar la verificación del email al usuario " + user.getEmail(), e);
        }
    }

    @Override
    @Transactional
    public Long verify(VerificationType type, String code) {
        return userVerificationDao.verify(type, code);
    }

    @Override
    @Transactional
    public int update(User user) {
        return userDao.update(user);
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String newPassword) {
        return userDao.changePassword(userId, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public int update(User user, byte[] bytes) {
        long imgId = imageService.update(user.getImgId(), bytes);
        user.setImgId(imgId);
        return userDao.update(user);
    }

    @Override
    @Transactional
    public int deleteById(long id) {
        return userDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> getFavoriteArtists(long userId) {
        return userDao.getFavoriteArtists(userId);
    }

    @Override
    @Transactional
    public boolean addFavoriteArtist(long userId, long artistId) {
        if (getFavoriteArtistsCount(userId) >= 5) {
            return false;
        }
        return userDao.addFavoriteArtist(userId, artistId);
    }

    @Override
    @Transactional
    public boolean removeFavoriteArtist(long userId, long artistId) {
        return userDao.removeFavoriteArtist(userId, artistId);
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
        if (getFavoriteAlbumsCount(userId) >= 5) {
            return false;
        }
        return userDao.addFavoriteAlbum(userId, albumId);
    }

    @Override
    @Transactional
    public boolean removeFavoriteAlbum(long userId, long albumId) {
        return userDao.removeFavoriteAlbum(userId, albumId);
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
        if (getFavoriteSongsCount(userId) >= 5) {
            return false;
        }
        return userDao.addFavoriteSong(userId, songId);
    }

    @Override
    @Transactional
    public boolean removeFavoriteSong(long userId, long songId) {
        return userDao.removeFavoriteSong(userId, songId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getFavoriteSongsCount(long userId) {
        return userDao.getFavoriteSongsCount(userId);
    }
}