package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.models.dtos.CreateUserDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.persistence.UserVerificationDao;
import ar.edu.itba.paw.exception.conflict.UserAlreadyExistsException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.exception.email.VerificationEmailException;
import ar.edu.itba.paw.exception.conflict.FavoriteLimitException;
import ar.edu.itba.paw.services.mappers.UserMapper;
import ar.edu.itba.paw.services.mappers.ArtistMapper;
import ar.edu.itba.paw.services.mappers.AlbumMapper;
import ar.edu.itba.paw.services.mappers.SongMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import ar.edu.itba.paw.services.utils.MergeUtils;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;
    private final UserVerificationDao userVerificationDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final SongMapper songMapper;

    private final EmailService emailService;
    private final NotificationService notificationService;
    private final ImageService imageService;

    public UserServiceImpl(UserDao userDao, UserVerificationDao userVerificationDao, PasswordEncoder passwordEncoder, UserMapper userMapper, ArtistMapper artistMapper, AlbumMapper albumMapper, SongMapper songMapper, EmailService emailService, NotificationService notificationService, ImageService imageService) {
        this.userDao = userDao;
        this.userVerificationDao = userVerificationDao;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.artistMapper = artistMapper;
        this.albumMapper = albumMapper;
        this.songMapper = songMapper;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.imageService = imageService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findUserById(Long id, Long loggedUserId) {
        UserDTO userDTO = userDao.findById(id).map(userMapper::toDTO).orElseThrow(() -> new UserNotFoundException(id));
        userDTO.setFollowedByLoggedUser(isFollowing(loggedUserId, id));
        return userDTO;
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
                .orElseThrow(() -> new UserNotFoundException(email, "email"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findByUsername(String username) {
        return userDao.findByUsername(username)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException(username, "username"));
    }

    @Override
    public Boolean usernameExists(String username) {
        return userDao.findByUsername(username).isPresent();
    }

    @Override
    public Boolean emailExists(String email) {
        return userDao.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public Void updateUserReviewAmount(Long userId) {
        LOGGER.info("Updating review amount for user with ID: {}", userId);
        userDao.updateUserReviewAmount(userId);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findByUsernameContaining(String sub, Integer pageNumber, Integer pageSize) {
        return userMapper.toDTOList(userDao.findByUsernameContaining(sub, pageNumber, pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userMapper.toDTOList(userDao.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return userMapper.toDTOList(userDao.findPaginated(filterType, page, pageSize));
    }

    @Override
    @Transactional
    public UserDTO create(CreateUserDTO createUserDTO) {
        LOGGER.info("Creating new user with username: {} and email: {}", createUserDTO.getUsername(), createUserDTO.getEmail());
        String hashedPassword = passwordEncoder.encode(createUserDTO.getPassword());

        if (usernameExists(createUserDTO.getUsername())) throw new UserAlreadyExistsException(createUserDTO.getUsername(), "username");
        if (emailExists(createUserDTO.getEmail())) throw new UserAlreadyExistsException(createUserDTO.getEmail(), "email");

        User createdUser = userDao.create(createUserDTO.getUsername(), createUserDTO.getEmail(), hashedPassword, imageService.findById(imageService.getDefaultProfileImgId())).orElseThrow(() -> new RuntimeException("Failed to create user"));
        createdUser.setPreferredLanguage(LocaleContextHolder.getLocale().getLanguage());
        this.createVerification(VerificationType.VERIFY_EMAIL, createdUser);
        LOGGER.info("Successfully created new user with ID: {}", createdUser.getId());
        return userMapper.toDTO(createdUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getFollowers(Long userId, Integer pageNumber, Integer pageSize) {
        if (userId == null || userDao.findById(userId).isEmpty()) throw new UserNotFoundException(userId);  
        return userMapper.toDTOList(userDao.getFollowers(userId, pageNumber, pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getFollowings(Long userId, Integer pageNumber, Integer pageSize) {
        if (userId == null || userDao.findById(userId).isEmpty()) throw new UserNotFoundException(userId);  
        return userMapper.toDTOList(userDao.getFollowings(userId, pageNumber, pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isFollowing(Long userId, Long otherId) {
        if (userId == null || otherId == null) return false;
        return userDao.isFollowing(userId, otherId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isAlbumFavorite(Long userId, Long albumId) {
        return userDao.isAlbumFavorite(userId, albumId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isArtistFavorite(Long userId, Long artistId) {
        return userDao.isArtistFavorite(userId, artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isSongFavorite(Long userId, Long songId) {
        return userDao.isSongFavorite(userId, songId);
    }

    @Override
    @Transactional
    public Integer createFollowing(Long userId, Long followingId) {
        LOGGER.info("Creating following relationship: User {} following User {}", userId, followingId);
        if (this.isFollowing(userId, followingId)) {
            LOGGER.info("Following relationship already exists");
            return 0;
        }
        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        User following = userDao.findById(followingId).orElseThrow(() -> new UserNotFoundException(followingId));

        int result = userDao.createFollowing(user, following);
        notificationService.notifyFollow(following, user);
        LOGGER.info("Following relationship created successfully");
        return result;
    }

    @Override
    @Transactional
    public Integer undoFollowing(Long userId, Long followingId) {
        LOGGER.info("Removing following relationship: User {} unfollowing User {}", userId, followingId);
        User following = userDao.findById(followingId).orElseThrow(() -> new UserNotFoundException(followingId));
        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        int result = userDao.undoFollowing(user, following);
        LOGGER.info("Following relationship removed successfully");
        return result;
    }

    @Override
    @Transactional
    public Void createVerification(VerificationType type, User user) {
        LOGGER.info("Creating verification for user: {} with type: {}", user.getId(), type);
        try {
            String verificationCode = UUID.randomUUID().toString();
            String encodedVerificationCode = URLEncoder.encode(verificationCode, StandardCharsets.UTF_8);
            userVerificationDao.startVerification(type, user, encodedVerificationCode);

            emailService.sendVerification(type, user, encodedVerificationCode);
            LOGGER.info("Verification created and email sent successfully");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send verification email to user: {}", user.getEmail(), e);
            throw new VerificationEmailException(user.getEmail(), e);
        }
        return null;
    }

    @Override
    @Transactional
    public Long verify(VerificationType type, String code) {
        LOGGER.info("Verifying user with type: {} and code: {}", type, code);
        Long result = userVerificationDao.verify(type, code);
        if (result != null) LOGGER.info("User verified successfully");
        else LOGGER.warn("User verification failed");
        return result;
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        LOGGER.info("Updating user with ID: {}", userDTO.getId());
        User existingUser = userDao.findById(userDTO.getId()).orElseThrow(() -> new UserNotFoundException(userDTO.getId()));
        
        if (userDTO.getImageId() != null) 
            existingUser.setImage(imageService.findById(userDTO.getImageId()));

        MergeUtils.mergeUserFields(existingUser, userDTO);
        User updatedUser = saveUser(existingUser);
        LOGGER.info("User with ID {} updated successfully", userDTO.getId());
        
        return userMapper.toDTO(updatedUser);
    }

    @Override
    @Transactional
    public Boolean changePassword(Long userId, String newPassword) {
        LOGGER.info("Changing password for user with ID: {}", userId);
        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        LOGGER.info("Password changed successfully for user with ID: {}", userId);
        return true;
    }

    @Override
    @Transactional
    public Integer deleteById(Long id) {
        LOGGER.info("Deleting user with ID: {}", id);
        int result = userDao.deleteById(id);
        if (result > 0) {
            LOGGER.info("User with ID: {} deleted successfully", id);
        } else {
            LOGGER.warn("Failed to delete user with ID: {}", id);
            throw new UserNotFoundException(id);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistDTO> getFavoriteArtists(Long userId) {
        return userDao.getFavoriteArtists(userId).stream().map(artistMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean addFavoriteArtist(Long userId, Long artistId) {
        LOGGER.info("Adding favorite artist with ID: {} for user with ID: {}", artistId, userId);
        if (getFavoriteArtistsCount(userId) >= 5) throw new FavoriteLimitException(userId);
        boolean result = userDao.addFavoriteArtist(userId, artistId);
        if (result) LOGGER.info("Favorite artist added successfully");
        else LOGGER.warn("Failed to add favorite artist");
        return result;
    }

    @Override
    @Transactional
    public Boolean removeFavoriteArtist(Long userId, Long artistId) {
        LOGGER.info("Removing favorite artist with ID: {} for user with ID: {}", artistId, userId);
        boolean result = userDao.removeFavoriteArtist(userId, artistId);
        if (result) LOGGER.info("Favorite artist removed successfully");
        else LOGGER.warn("Failed to remove favorite artist");
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getFavoriteArtistsCount(Long userId) {
        return userDao.getFavoriteArtistsCount(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDTO> getFavoriteAlbums(Long userId) {
        return userDao.getFavoriteAlbums(userId).stream().map(albumMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean addFavoriteAlbum(Long userId, Long albumId) {
        LOGGER.info("Adding favorite album with ID: {} for user with ID: {}", albumId, userId);
        if (getFavoriteAlbumsCount(userId) >= 5) throw new FavoriteLimitException(userId);
        boolean result = userDao.addFavoriteAlbum(userId, albumId);
        if (result) LOGGER.info("Favorite album added successfully");
        else LOGGER.warn("Failed to add favorite album");
        return result;
    }

    @Override
    @Transactional
    public Boolean removeFavoriteAlbum(Long userId, Long albumId) {
        LOGGER.info("Removing favorite album with ID: {} for user with ID: {}", albumId, userId);
        boolean result = userDao.removeFavoriteAlbum(userId, albumId);
        if (result) LOGGER.info("Favorite album removed successfully");
        else LOGGER.warn("Failed to remove favorite album");
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getFavoriteAlbumsCount(Long userId) {
        return userDao.getFavoriteAlbumsCount(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongDTO> getFavoriteSongs(Long userId) {
        return userDao.getFavoriteSongs(userId).stream().map(songMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean addFavoriteSong(Long userId, Long songId) {
        LOGGER.info("Adding favorite song with ID: {} for user with ID: {}", songId, userId);
        if (getFavoriteSongsCount(userId) >= 5) throw new FavoriteLimitException(userId);
        boolean result = userDao.addFavoriteSong(userId, songId);
        if (result) LOGGER.info("Favorite song added successfully");
        else LOGGER.warn("Failed to add favorite song");
        return result;
    }

    @Override
    @Transactional
    public Boolean removeFavoriteSong(Long userId, Long songId) {
        LOGGER.info("Removing favorite song with ID: {} for user with ID: {}", songId, userId);
        boolean result = userDao.removeFavoriteSong(userId, songId);
        if (result) LOGGER.info("Favorite song removed successfully");
        else LOGGER.warn("Failed to remove favorite song");
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getFavoriteSongsCount(Long userId) {
        return userDao.getFavoriteSongsCount(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getRecommendedUsers(Long userId, Integer pageNumber, Integer pageSize) {
        LOGGER.info("Getting recommended users for user with ID: {}", userId);
        if (userId == null || userDao.findById(userId).isEmpty()) {
            LOGGER.warn("Invalid user ID: {}", userId);
            throw new UserNotFoundException(userId);
        }
        
        List<User> recommendedUsers = userDao.getRecommendedUsers(userId, pageNumber, pageSize);
        LOGGER.info("Found {} recommended users for user with ID: {}", recommendedUsers.size(), userId);
        return userMapper.toDTOList(recommendedUsers);
    }

    private User saveUser(User user) {
        return userDao.updateUser(user.getId(), user).orElseThrow(() -> new UserNotFoundException(user.getId()));
    }
}