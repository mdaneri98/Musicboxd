package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.dtos.CreateUserDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.persistence.UserVerificationDao;
import ar.edu.itba.paw.services.exception.UserAlreadyExistsException;
import ar.edu.itba.paw.services.exception.UserNotFoundException;
import ar.edu.itba.paw.services.exception.VerificationEmailException;
import ar.edu.itba.paw.services.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;
    private final UserVerificationDao userVerificationDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private final EmailService emailService;
    private final ImageService imageService;
    private final NotificationService notificationService;

    public UserServiceImpl(UserDao userDao, UserVerificationDao userVerificationDao, PasswordEncoder passwordEncoder, UserMapper userMapper, EmailService emailService, ImageService imageService, NotificationService notificationService) {
        this.userDao = userDao;
        this.userVerificationDao = userVerificationDao;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.imageService = imageService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findUserById(Long id) {
        return userDao.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUsers() {
        return userDao.countUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findByEmail(String email) {
        return userDao.findByEmail(email)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findByUsername(String username) {
        return userDao.findByUsername(username)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
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
    public List<UserDTO> findByUsernameContaining(String sub, int pageNumber, int pageSize) {
        return userMapper.toDTOList(userDao.findByUsernameContaining(sub, pageNumber, pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userMapper.toDTOList(userDao.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findPaginated(FilterType filterType, int page, int pageSize) {
        return userMapper.toDTOList(userDao.findPaginated(filterType, page, pageSize));
    }

    @Override
    @Transactional
    public UserDTO create(CreateUserDTO createUserDTO) {
        LOGGER.info("Creating new user with username: {} and email: {}", createUserDTO.getUsername(), createUserDTO.getEmail());
        String hashedPassword = passwordEncoder.encode(createUserDTO.getPassword());

        /* Caso que el usuario se haya registrado anteriormente sin datos de usuario, y unicamente con email. */
        Optional<User> emailOptUser = userDao.findByEmail(createUserDTO.getEmail());
        Optional<User> usernameOptUser = userDao.findByUsername(createUserDTO.getUsername());

        if (emailOptUser.isPresent()) {
            if (emailOptUser.get().getUsername() == null){
                User user = emailOptUser.get();
                user.setUsername(createUserDTO.getUsername());
                user.setPassword(createUserDTO.getPassword());
                user.setEmail(createUserDTO.getEmail());
                userDao.updateUser(user.getId(), user);
                LOGGER.info("Updated existing user with email: {}", createUserDTO.getEmail());
                return userMapper.toDTO(user);
            } else {
                LOGGER.warn("Attempt to create user with existing email: {}", createUserDTO.getEmail());
                throw new UserAlreadyExistsException("El correo " + createUserDTO.getEmail() + " ya está en uso.");
            }
        }
        if (usernameOptUser.isPresent()) {
            LOGGER.warn("Attempt to create user with existing username: {}", createUserDTO.getUsername());
            throw new UserAlreadyExistsException("El usuario " + createUserDTO.getUsername() + " ya está en uso.");
        }

        Optional<Image> optionalImage = imageService.findById(imageService.getDefaultProfileImgId());
        if (optionalImage.isEmpty())
            throw new IllegalArgumentException("La imagen profile-default no existe.");

        Optional<User> userOpt = userDao.create(createUserDTO.getUsername(), createUserDTO.getEmail(), hashedPassword);
        if (userOpt.isPresent()) {
            User createdUser = userOpt.get();
            createdUser.setPreferredLanguage(LocaleContextHolder.getLocale().getLanguage());
            this.createVerification(VerificationType.VERIFY_EMAIL, createdUser);
            LOGGER.info("Successfully created new user with ID: {}", createdUser.getId());
            return userMapper.toDTO(createdUser);
        } else {
            LOGGER.error("Failed to create new user with username: {} and email: {}", createUserDTO.getUsername(), createUserDTO.getEmail());
            throw new RuntimeException("Failed to create user");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getFollowers(Long userId, Integer pageNumber, Integer pageSize) {
        if (userId == null || userDao.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        return userMapper.toDTOList(userDao.getFollowers(userId, pageNumber, pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getFollowings(Long userId, Integer pageNumber, Integer pageSize) {
        if (userId == null || userDao.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        return userMapper.toDTOList(userDao.getFollowings(userId, pageNumber, pageSize));
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
    public int createFollowing(Long userId, Long followingId) {
        LOGGER.info("Creating following relationship: User {} following User {}", userId, followingId);
        if (this.isFollowing(userId, followingId)) {
            LOGGER.info("Following relationship already exists");
            return 0;
        }
        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        Optional<User> followingOpt = userDao.findById(followingId);
        if (followingOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID " + followingId + " not found");
        }
        User user = userOpt.get();
        User following = followingOpt.get();
        int result = userDao.createFollowing(user, following);
        notificationService.notifyFollow(following, user);
        LOGGER.info("Following relationship created successfully");
        return result;
    }

    @Override
    @Transactional
    public int undoFollowing(Long userId, Long followingId) {
        LOGGER.info("Removing following relationship: User {} unfollowing User {}", userId, followingId);
        Optional<User> followingOpt = userDao.findById(followingId);
        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        if (followingOpt.isEmpty()) {
            throw new UserNotFoundException("User with ID " + followingId + " not found");
        }
        User user = userOpt.get();
        User following = followingOpt.get();
        int result = userDao.undoFollowing(user, following);
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

            emailService.sendVerification(type, user, encodedVerificationCode);
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
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        LOGGER.info("Updating user with ID: {}", userId);
        
        User existingUser = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
        validateEmailUniqueness(userId, userDTO.getEmail());
        validateUsernameUniqueness(userId, userDTO.getUsername());
        mergeUserFields(existingUser, userDTO);
        
        User updatedUser = saveUser(existingUser);
        LOGGER.info("User with ID {} updated successfully", userId);
        
        return userMapper.toDTO(updatedUser);
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String newPassword) {
        LOGGER.info("Changing password for user with ID: {}", userId);
        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            LOGGER.info("Password changed successfully for user with ID: {}", userId);
            return true;
        } else {
            LOGGER.warn("Failed to change password for user with ID: {}", userId);
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
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

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getRecommendedUsers(Long userId, int pageNumber, int pageSize) {
        LOGGER.info("Getting recommended users for user with ID: {}", userId);
        if (userId == null || userDao.findById(userId).isEmpty()) {
            LOGGER.warn("Invalid user ID: {}", userId);
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        
        List<User> recommendedUsers = userDao.getRecommendedUsers(userId, pageNumber, pageSize);
        LOGGER.info("Found {} recommended users for user with ID: {}", recommendedUsers.size(), userId);
        return userMapper.toDTOList(recommendedUsers);
    }

    private void validateUsernameUniqueness(Long userId, String username) {
        if (username == null) return;

        Optional<User> existingUser = userDao.findByUsername(username);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
            throw new UserAlreadyExistsException("Username " + username + " is already in use");
        }
    }

    private void validateEmailUniqueness(Long userId, String email) {
        if (email == null) return;

        Optional<User> existingUser = userDao.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
            throw new UserAlreadyExistsException("Email " + email + " is already in use");
        }
    }

    private void mergeUserFields(User existingUser, UserDTO userDTO) {
        mergeBasicFields(existingUser, userDTO);
        mergeNotificationSettings(existingUser, userDTO);
        existingUser.setUpdatedAt(LocalDateTime.now());
    }

    private void mergeBasicFields(User existingUser, UserDTO userDTO) {
        setFieldIfNotNull(existingUser::setUsername, userDTO.getUsername());
        setFieldIfNotNull(existingUser::setEmail, userDTO.getEmail());
        setFieldIfNotNull(existingUser::setName, userDTO.getName());
        setFieldIfNotNull(existingUser::setBio, userDTO.getBio());
        setFieldIfNotNull(existingUser::setImageId, userDTO.getImageId());
        setFieldIfNotNull(existingUser::setPreferredLanguage, userDTO.getPreferredLanguage());
        setFieldIfNotNull(existingUser::setTheme, userDTO.getPreferredTheme());
    }

    private void mergeNotificationSettings(User existingUser, UserDTO userDTO) {
        setFieldIfNotNull(existingUser::setFollowNotificationsEnabled, userDTO.getHasFollowNotificationsEnabled());
        setFieldIfNotNull(existingUser::setLikeNotificationsEnabled, userDTO.getHasLikeNotificationsEnabled());
        setFieldIfNotNull(existingUser::setCommentNotificationsEnabled, userDTO.getHasCommentsNotificationsEnabled());
        setFieldIfNotNull(existingUser::setReviewNotificationsEnabled, userDTO.getHasReviewsNotificationsEnabled());
    }

    private <T> void setFieldIfNotNull(java.util.function.Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private User saveUser(User user) {
        return userDao.updateUser(user.getId(), user)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + user.getId() + " not found"));
    }

}