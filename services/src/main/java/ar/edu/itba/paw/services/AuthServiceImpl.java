package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.RefreshToken;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.dtos.LoginRequestDTO;
import ar.edu.itba.paw.models.dtos.LoginResponseDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.persistence.RefreshTokenDao;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.services.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    private final UserService userService;
    private final UserDao userDao;
    private final JwtService jwtService;
    private final RefreshTokenDao refreshTokenDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserService userService, UserDao userDao, JwtService jwtService, 
                          RefreshTokenDao refreshTokenDao, PasswordEncoder passwordEncoder,
                          UserMapper userMapper) {
        this.userService = userService;
        this.userDao = userDao;
        this.jwtService = jwtService;
        this.refreshTokenDao = refreshTokenDao;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        LOGGER.info("Attempting login for username: {}", loginRequest.getUsername());
        
        try {
            // Find user by username
            Optional<User> userOpt = userDao.findByUsername(loginRequest.getUsername());
            if (userOpt.isEmpty()) {
                LOGGER.warn("User not found: {}", loginRequest.getUsername());
                throw new RuntimeException("Invalid credentials");
            }
            
            User user = userOpt.get();
            
            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                LOGGER.warn("Invalid password for username: {}", loginRequest.getUsername());
                throw new RuntimeException("Invalid credentials");
            }
            
            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.isModerator());
            String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());
            
            // Hash refresh token before storing
            String hashedRefreshToken = hashToken(refreshToken);
            
            // Store refresh token in database
            RefreshToken tokenEntity = new RefreshToken(hashedRefreshToken, user.getId(), 
                    LocalDateTime.now().plusDays(30));
            refreshTokenDao.save(tokenEntity);
            
            LOGGER.info("Successful login for user: {}", user.getUsername());
            
            UserDTO userDTO = userMapper.toDTO(user);
            return new LoginResponseDTO(accessToken, refreshToken, userDTO);
            
        } catch (UserNotFoundException e) {
            LOGGER.warn("User not found: {}", loginRequest.getUsername());
            throw new RuntimeException("Invalid credentials");
        }
    }

    @Override
    @Transactional
    public LoginResponseDTO refresh(String refreshToken) {
        LOGGER.info("Attempting token refresh");
        
        // Validate refresh token
        if (!jwtService.validateRefreshToken(refreshToken)) {
            LOGGER.warn("Invalid refresh token format");
            throw new RuntimeException("Invalid refresh token");
        }
        
        // Hash the token to check against database
        String hashedToken = hashToken(refreshToken);
        
        // Check if token exists and is valid in database
        Optional<RefreshToken> tokenEntity = refreshTokenDao.findByToken(hashedToken);
        if (tokenEntity.isEmpty() || !tokenEntity.get().isValid()) {
            LOGGER.warn("Refresh token not found or invalid");
            throw new RuntimeException("Invalid refresh token");
        }
        
        // Extract user info from token
        Long userId = jwtService.extractUserId(refreshToken);
        String username = jwtService.extractUsername(refreshToken);
        
        // Revoke old refresh token (token rotation)
        refreshTokenDao.revokeToken(hashedToken);
        
        // Generate new tokens
        Optional<User> userOpt = userDao.findById(userId);
        if (userOpt.isEmpty()) {
            LOGGER.warn("User not found for refresh token: {}", userId);
            throw new RuntimeException("Invalid refresh token");
        }
        User user = userOpt.get();
        
        String newAccessToken = jwtService.generateAccessToken(userId, username, user.isModerator());
        String newRefreshToken = jwtService.generateRefreshToken(userId, username);
        
        // Store new refresh token
        String newHashedToken = hashToken(newRefreshToken);
        RefreshToken newTokenEntity = new RefreshToken(newHashedToken, userId, 
                LocalDateTime.now().plusDays(30));
        refreshTokenDao.save(newTokenEntity);
        
        LOGGER.info("Token refresh successful for user: {}", username);
        
        UserDTO userDTO = userMapper.toDTO(user);
        return new LoginResponseDTO(newAccessToken, newRefreshToken, userDTO);
    }

    @Override
    @Transactional
    public boolean logout(String refreshToken) {
        LOGGER.info("Attempting logout");
        
        try {
            String hashedToken = hashToken(refreshToken);
            boolean revoked = refreshTokenDao.revokeToken(hashedToken);
            
            if (revoked) {
                LOGGER.info("Logout successful");
            } else {
                LOGGER.warn("Refresh token not found for logout");
            }
            
            return revoked;
        } catch (Exception e) {
            LOGGER.error("Error during logout", e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateRefreshToken(String refreshToken) {
        try {
            // Validate JWT format
            if (!jwtService.validateRefreshToken(refreshToken)) {
                return false;
            }
            
            // Check database
            String hashedToken = hashToken(refreshToken);
            return refreshTokenDao.isValidToken(hashedToken);
            
        } catch (Exception e) {
            LOGGER.error("Error validating refresh token", e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String accessToken) {
        try {
            if (!jwtService.validateAccessToken(accessToken)) {
                throw new RuntimeException("Invalid access token");
            }
            
            Long userId = jwtService.extractUserId(accessToken);
            return userService.findUserById(userId, 0L);
            
        } catch (Exception e) {
            LOGGER.error("Error getting current user", e);
            throw new RuntimeException("Invalid access token");
        }
    }

    /**
     * Hash token using SHA-256 for secure storage
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
