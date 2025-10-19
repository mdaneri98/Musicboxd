package ar.edu.itba.paw.models.dtos;

public class LoginResponseDTO {
    
    private String accessToken;
    private String refreshToken;
    private UserDTO user;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String accessToken, String refreshToken, UserDTO user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
