package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification")
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_id_seq")
    @SequenceGenerator(sequenceName = "verification_id_seq", name = "verification_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    @Column(name = "vtype", nullable = false)
    private String verificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public UserVerification() {
        // Constructor vacío necesario para JPA
    }

    public UserVerification(Long id, String code, LocalDateTime expireDate, String verificationType, User user) {
        this.id = id;
        this.code = code;
        this.expireDate = expireDate;
        this.verificationType = verificationType;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }
}
