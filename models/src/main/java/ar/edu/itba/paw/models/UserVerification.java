package ar.edu.itba.paw.models;

import java.sql.Timestamp;

public class UserVerification {

    private Long id;
    private Long user_id;
    private String code;
    private Timestamp expireDate;

    public UserVerification(Long id, Long user_id, String code, Timestamp expireDate) {
        this.id = id;
        this.user_id = user_id;
        this.code = code;
        this.expireDate = expireDate;
    }

    public Long getId() {
        return id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getCode() {
        return code;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }
}
