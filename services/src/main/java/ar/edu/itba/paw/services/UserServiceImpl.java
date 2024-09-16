package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.persistence.UserVerificationDao;
import ar.edu.itba.paw.services.exception.UserAlreadyExistsException;
import ar.edu.itba.paw.services.exception.VerificationEmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public UserServiceImpl(UserDao userDao, UserVerificationDao userVerificationDao, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userDao = userDao;
        this.userVerificationDao = userVerificationDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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
    public int incrementReviewAmount(User user) {
        user.incrementReviewAmount();
        return userDao.update(user);
    }

    @Override
    public int decrementReviewAmount(User user) {
        user.decrementReviewAmount();
        return userDao.update(user);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public int create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        /* Caso que el usuario se haya registrado anteriormente sin datos de usuario, y unicamente con email. */
        Optional<User> optUser = this.findByEmail(user.getEmail());
        if (optUser.isPresent()) {
            if (!optUser.get().getUsername().isEmpty())
                throw new UserAlreadyExistsException("El correo " + user.getEmail() + " ya está en uso.");
            user.setId(optUser.get().getId());
            return this.update(user);
        }

        int rowsChanged = userDao.create(user);
        if (rowsChanged > 0)
            user = userDao.findByEmail(user.getEmail()).orElseThrow();
            this.createVerification(user);
        return rowsChanged;
    }

    @Override
    public int createFollowing(User userId, long followingId) {
        return userDao.createFollowing(userId, findById(followingId).get());
    }

    @Override
    public int undoFollowing(User userId, long followingId) {
        return userDao.undoFollowing(userId, findById(followingId).get());
    }

    @Override
    public void createVerification(User user) {
        try {
            String verificationCode = UUID.randomUUID().toString();

            // Codifica el código de verificación para asegurarte de que sea seguro para la URL
            String encodedVerificationCode = URLEncoder.encode(verificationCode, StandardCharsets.UTF_8);

            userVerificationDao.startVerification(user, encodedVerificationCode);
            emailService.sendVerification(user.getEmail(), encodedVerificationCode);
        } catch (MessagingException e) {
            logger.error("Error al enviar el correo de verificación al usuario: {}", user.getEmail(), e);
            throw new VerificationEmailException("No se pudo enviar la verificación del email al usuario " + user.getEmail(), e);
        }
    }

    @Override
    public boolean verify(String code) {
        return userVerificationDao.verify(code);
    }

    @Override
    public int update(User user) {
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
