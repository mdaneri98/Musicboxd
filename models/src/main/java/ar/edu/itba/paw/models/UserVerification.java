package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "verification")
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "expire_date", nullable = false)
    private Timestamp expireDate;

    @Column(name = "vtype", nullable = false)
    private String verificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public UserVerification() {
        // Constructor vacío necesario para JPA
    }

    public UserVerification(Long id, Long userId, String code, Timestamp expireDate, String verificationType) {
        this.id = id;
        this.userId = userId;
        this.code = code;
        this.expireDate = expireDate;
        this.verificationType = verificationType;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
