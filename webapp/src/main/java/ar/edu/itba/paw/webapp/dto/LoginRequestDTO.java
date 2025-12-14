package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LoginRequestDTO {
    
    @NotBlank(message = "{login.username.NotBlank}")
    @Size(min = 4, max = 50, message = "{login.username.Size}")
    private String username;
    
    @NotBlank(message = "{login.password.NotBlank}")
    @Size(min = 6, message = "{login.password.Size}")
    private String password;

    public LoginRequestDTO() {}

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

