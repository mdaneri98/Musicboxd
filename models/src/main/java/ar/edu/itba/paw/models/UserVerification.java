package ar.edu.itba.paw.models;

import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification")
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verify_user_id_seq")
    @SequenceGenerator(sequenceName = "verify_user_id_seq", name = "verify_user_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    @Column(name = "vtype", nullable = false)
    private String verificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;

    public UserVerification() {
        // Constructor vacío necesario para JPA
    }

    public UserVerification(Long id, String code, LocalDateTime expireDate, String verificationType, UserJpaEntity user) {
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

    public UserJpaEntity getUser() {
        return user;
    }

    public void setUser(UserJpaEntity user) {
        this.user = user;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }
}
