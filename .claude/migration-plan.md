# Plan de Migración a Arquitectura Hexagonal - Musicboxd Backend

**Estrategia**: Big Bang Atómico por Entidad
**Estado inicial**: Arquitectura en capas tradicional (sin cambios hexagonales)
**Objetivo**: Arquitectura hexagonal pura sin código legacy residual

---

## Principios Fundamentales

### 1. Zero Legacy Residual
- ❌ NO coexistencia temporal de código
- ❌ NO mappers de compatibilidad (LegacyMapper)
- ❌ NO interfaces duplicadas (Dao + Repository)
- ✅ Al finalizar cada entidad: arquitectura 100% limpia

### 2. Migración Atómica
- Cada entidad completa TODAS sus capas antes de pasar a la siguiente
- Un solo commit atómico por entidad (fácil rollback)
- Compilación exitosa + tests pasando después de cada entidad

### 3. Orden Estricto de Dependencias
```
User (sin dependencias de dominio)
  ↓
Artist (sin dependencias de dominio)
  ↓
Album (depende de Artist)
  ↓
Song (depende de Album + Artist)
  ↓
Review (depende de User + Album/Artist/Song)
  ↓
Comment (depende de Review + User)
```

---

## Arquitectura Objetivo

```
┌─────────────────────────────────────────────────────────────┐
│      PRESENTATION (webapp/) - REST Controllers               │
│  • ReviewController (usa Use Cases)                          │
│  • AlbumController (usa Use Cases)                           │
└──────────────────┬──────────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────────┐
│         APPLICATION (services/)                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Use Cases (Input Ports):                             │   │
│  │  • CreateUser, UpdateUser, DeleteUser                │   │
│  │  • CreateAlbum, UpdateAlbum, DeleteAlbum             │   │
│  │  • CreateReview, UpdateReview, BlockReview           │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Domain Services:                                      │   │
│  │  • RatingService (cálculo de ratings)                │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Event Handlers:                                       │   │
│  │  • ArtistDeletedHandler → cascade Albums/Reviews     │   │
│  │  • AlbumDeletedHandler → cascade Songs/Reviews       │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────┬────────────────┬─────────────────────────┘
                   │ usan ↓         │ publican ↓
┌──────────────────▼────────────────▼─────────────────────────┐
│            DOMAIN (models/)                                  │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Rich Entities:                                        │   │
│  │  • User (validación, contadores encapsulados)        │   │
│  │  • Album (factory methods, reglas de negocio)        │   │
│  │  • Review (polimorfismo, métodos de dominio)         │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Value Objects:                                        │   │
│  │  • Rating (validación 1-5, cálculo)                  │   │
│  │  • UserId, AlbumId, ReviewId (type-safe IDs)         │   │
│  │  • Email (validación)                                 │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Repository Ports (Output Ports):                     │   │
│  │  • UserRepository                                     │   │
│  │  • AlbumRepository                                    │   │
│  │  • ReviewRepository                                   │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Domain Events:                                        │   │
│  │  • ArtistDeleted, AlbumDeleted, SongDeleted          │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────┬──────────────────────────────────────────┘
                   │ implementados por ↓
┌──────────────────▼──────────────────────────────────────────┐
│        INFRASTRUCTURE (persistence/ + webapp/)               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ JPA Adapters (persistence/):                         │   │
│  │  • UserRepositoryJpa (implementa UserRepository)     │   │
│  │  • AlbumRepositoryJpa (implementa AlbumRepository)   │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ JPA Entities (models/infrastructure/jpa/):           │   │
│  │  • UserJpaEntity (solo persistencia, sin lógica)     │   │
│  │  • AlbumJpaEntity (solo persistencia)                │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Mappers (persistence/mappers/):                      │   │
│  │  • UserMapper (Domain ↔ JPA)                         │   │
│  │  • AlbumMapper (Domain ↔ JPA)                        │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Infrastructure Adapters (webapp/adapters/):          │   │
│  │  • EmailServiceAdapter (implementa EmailSender port) │   │
│  │  • EventPublisher (implementa DomainEventPublisher)  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## Reglas Arquitectónicas Inquebrantables

1. **Ninguna entidad de dominio tiene anotaciones JPA**
   - Domain entities son POJOs puros
   - JPA entities viven en `models/infrastructure/jpa/`

2. **Ningún Use Case depende de Dao**
   - Use cases solo dependen de Repository ports
   - Daos se eliminan completamente

3. **Ningún Service legacy sobrevive**
   - Servicios tradicionales (AlbumService, ReviewService) se eliminan
   - Reemplazados por Use Cases + Domain Services

4. **Relaciones entre agregados solo por ID**
   - `Album` tiene `ArtistId`, NO `Artist` entity
   - Queries explícitas: `artistRepository.findById(album.getArtistId())`

5. **No hay coexistencia temporal**
   - Al migrar Album: se elimina `models/Album.java` legacy
   - Al migrar Album: se elimina `AlbumDao` + `AlbumJpaDao`
   - NO existe `LegacyAlbumMapper`

6. **Cada entidad termina completamente antes de avanzar**
   - Domain → Infrastructure → Application → Delete Legacy → Tests
   - Compilación exitosa obligatoria antes de siguiente entidad

---

## Timeline de Migración (Orden Obligatorio)

### Entidad 1: User (4-5 horas)
**Complejidad**: Media (relaciones ManyToMany, contadores)
**Archivos afectados**: ~15-20

### Entidad 2: Artist (3-4 horas)
**Complejidad**: Baja (agregado simple)
**Archivos afectados**: ~10-15

### Entidad 3: Album (4-5 horas)
**Complejidad**: Media (depende de Artist)
**Archivos afectados**: ~15-20

### Entidad 4: Song (3-4 horas)
**Complejidad**: Media (depende de Album + Artist)
**Archivos afectados**: ~12-18

### Entidad 5: Review (6-8 horas)
**Complejidad**: Alta (polimorfismo, herencia JOINED)
**Archivos afectados**: ~25-30

### Entidad 6: Comment (3-4 horas)
**Complejidad**: Baja (depende de Review + User)
**Archivos afectados**: ~8-12

**Total estimado**: 25-30 horas de migración limpia

---

## Fase 1: User

### Paso 1.1 - Domain Layer

**Archivos a crear**:

1. `models/src/main/java/ar/edu/itba/paw/domain/user/UserId.java`
```java
public class UserId {
    private final Long value;

    public UserId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        this.value = value;
    }

    public Long getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId userId = (UserId) o;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }
}
```

2. `models/src/main/java/ar/edu/itba/paw/domain/user/Email.java` (Value Object)
```java
import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.value = value.toLowerCase();
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }
}
```

3. `models/src/main/java/ar/edu/itba/paw/domain/user/User.java` (Rich Entity)
```java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private final UserId id;
    private String username;
    private Email email;
    private String password;
    private boolean verified;
    private boolean isModerator;
    private int reviewAmount;
    private int followerAmount;
    private int followingAmount;
    private final LocalDateTime createdAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // Private constructor - enforce factory methods
    private User(UserId id, String username, Email email, String password,
                 boolean verified, boolean isModerator, int reviewAmount,
                 int followerAmount, int followingAmount, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.isModerator = isModerator;
        this.reviewAmount = reviewAmount;
        this.followerAmount = followerAmount;
        this.followingAmount = followingAmount;
        this.createdAt = createdAt;
    }

    // Factory method for new users
    public static User create(String username, Email email, String hashedPassword) {
        validateUsername(username);
        return new User(null, username, email, hashedPassword, false, false,
                       0, 0, 0, LocalDateTime.now());
    }

    // Factory method for reconstitution from persistence
    public static User reconstitute(UserId id, String username, Email email,
                                   String password, boolean verified,
                                   boolean isModerator, int reviewAmount,
                                   int followerAmount, int followingAmount,
                                   LocalDateTime createdAt) {
        if (id == null) throw new IllegalArgumentException("ID required for reconstitution");
        return new User(id, username, email, password, verified, isModerator,
                       reviewAmount, followerAmount, followingAmount, createdAt);
    }

    // Business behavior
    public void verify() {
        if (this.verified) {
            throw new IllegalStateException("User already verified");
        }
        this.verified = true;
    }

    public void promoteToModerator() {
        if (this.isModerator) {
            throw new IllegalStateException("User already moderator");
        }
        this.isModerator = true;
    }

    public void incrementReviewCount() {
        this.reviewAmount++;
    }

    public void decrementReviewCount() {
        if (this.reviewAmount > 0) {
            this.reviewAmount--;
        }
    }

    public void incrementFollowerCount() {
        this.followerAmount++;
    }

    public void decrementFollowerCount() {
        if (this.followerAmount > 0) {
            this.followerAmount--;
        }
    }

    public void incrementFollowingCount() {
        this.followingAmount++;
    }

    public void decrementFollowingCount() {
        if (this.followingAmount > 0) {
            this.followingAmount--;
        }
    }

    public void changePassword(String newHashedPassword) {
        if (newHashedPassword == null || newHashedPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        this.password = newHashedPassword;
    }

    // Validation
    private static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be 3-50 characters");
        }
    }

    // Domain events
    protected void addEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> getEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    // Getters (immutability)
    public UserId getId() { return id; }
    public String getUsername() { return username; }
    public Email getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isVerified() { return verified; }
    public boolean isModerator() { return isModerator; }
    public int getReviewAmount() { return reviewAmount; }
    public int getFollowerAmount() { return followerAmount; }
    public int getFollowingAmount() { return followingAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

4. `models/src/main/java/ar/edu/itba/paw/domain/user/UserRepository.java` (Port)
```java
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    // CRUD
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    Optional<User> findByUsername(String username);
    User save(User user);
    void delete(UserId id);

    // Queries
    List<User> findAll(Integer page, Integer size, String orderBy, String order);
    Long countAll();

    // Followers/Following (manejados en repository, no en entity)
    void addFollower(UserId userId, UserId followerId);
    void removeFollower(UserId userId, UserId followerId);
    boolean isFollowing(UserId userId, UserId followedId);
    List<UserId> getFollowerIds(UserId userId, Integer page, Integer size);
    List<UserId> getFollowingIds(UserId userId, Integer page, Integer size);
    Long countFollowers(UserId userId);
    Long countFollowing(UserId userId);

    // Favorites (Album, Artist, Song) - solo IDs
    void addFavoriteAlbum(UserId userId, Long albumId);
    void removeFavoriteAlbum(UserId userId, Long albumId);
    boolean isAlbumFavorite(UserId userId, Long albumId);
    List<Long> getFavoriteAlbumIds(UserId userId, Integer page, Integer size);

    void addFavoriteArtist(UserId userId, Long artistId);
    void removeFavoriteArtist(UserId userId, Long artistId);
    boolean isArtistFavorite(UserId userId, Long artistId);
    List<Long> getFavoriteArtistIds(UserId userId, Integer page, Integer size);

    void addFavoriteSong(UserId userId, Long songId);
    void removeFavoriteSong(UserId userId, Long songId);
    boolean isSongFavorite(UserId userId, Long songId);
    List<Long> getFavoriteSongIds(UserId userId, Integer page, Integer size);
}
```

5. `models/src/main/java/ar/edu/itba/paw/domain/events/DomainEvent.java` (Base interface)
```java
import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getOccurredAt();
}
```

---

### Paso 1.2 - Infrastructure Layer

**Archivos a crear**:

1. `models/src/main/java/ar/edu/itba/paw/infrastructure/jpa/UserJpaEntity.java`
```java
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cuser")
class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cuser_id_seq")
    @SequenceGenerator(sequenceName = "cuser_id_seq", name = "cuser_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean verified;

    @Column(name = "ismoderator")
    private Boolean isModerator;

    @Column(name = "review_amount")
    private Integer reviewAmount;

    @Column(name = "follower_amount")
    private Integer followerAmount;

    @Column(name = "following_amount")
    private Integer followingAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Join tables for favorites (ManyToMany sin entidad inversa)
    @ElementCollection
    @CollectionTable(name = "favorite_album", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "album_id")
    private List<Long> favoriteAlbumIds;

    @ElementCollection
    @CollectionTable(name = "favorite_artist", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "artist_id")
    private List<Long> favoriteArtistIds;

    @ElementCollection
    @CollectionTable(name = "favorite_song", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "song_id")
    private List<Long> favoriteSongIds;

    // Followers/Following join table
    @ElementCollection
    @CollectionTable(name = "follow", joinColumns = @JoinColumn(name = "follower_id"))
    @Column(name = "followed_id")
    private List<Long> followingIds;

    // No-arg constructor for JPA
    UserJpaEntity() {}

    // Package-private getters/setters
    Long getId() { return id; }
    void setId(Long id) { this.id = id; }

    String getUsername() { return username; }
    void setUsername(String username) { this.username = username; }

    String getEmail() { return email; }
    void setEmail(String email) { this.email = email; }

    String getPassword() { return password; }
    void setPassword(String password) { this.password = password; }

    Boolean getVerified() { return verified; }
    void setVerified(Boolean verified) { this.verified = verified; }

    Boolean getIsModerator() { return isModerator; }
    void setIsModerator(Boolean isModerator) { this.isModerator = isModerator; }

    Integer getReviewAmount() { return reviewAmount; }
    void setReviewAmount(Integer reviewAmount) { this.reviewAmount = reviewAmount; }

    Integer getFollowerAmount() { return followerAmount; }
    void setFollowerAmount(Integer followerAmount) { this.followerAmount = followerAmount; }

    Integer getFollowingAmount() { return followingAmount; }
    void setFollowingAmount(Integer followingAmount) { this.followingAmount = followingAmount; }

    LocalDateTime getCreatedAt() { return createdAt; }
    void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    List<Long> getFavoriteAlbumIds() { return favoriteAlbumIds; }
    void setFavoriteAlbumIds(List<Long> favoriteAlbumIds) { this.favoriteAlbumIds = favoriteAlbumIds; }

    List<Long> getFavoriteArtistIds() { return favoriteArtistIds; }
    void setFavoriteArtistIds(List<Long> favoriteArtistIds) { this.favoriteArtistIds = favoriteArtistIds; }

    List<Long> getFavoriteSongIds() { return favoriteSongIds; }
    void setFavoriteSongIds(List<Long> favoriteSongIds) { this.favoriteSongIds = favoriteSongIds; }

    List<Long> getFollowingIds() { return followingIds; }
    void setFollowingIds(List<Long> followingIds) { this.followingIds = followingIds; }
}
```

2. `persistence/src/main/java/ar/edu/itba/paw/persistence/mappers/UserMapper.java`
```java
import ar.edu.itba.paw.domain.user.*;
import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
class UserMapper {

    User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;

        return User.reconstitute(
            new UserId(entity.getId()),
            entity.getUsername(),
            new Email(entity.getEmail()),
            entity.getPassword(),
            entity.getVerified(),
            entity.getIsModerator(),
            entity.getReviewAmount(),
            entity.getFollowerAmount(),
            entity.getFollowingAmount(),
            entity.getCreatedAt()
        );
    }

    UserJpaEntity toEntity(User domain) {
        if (domain == null) return null;

        UserJpaEntity entity = new UserJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail().getValue());
        entity.setPassword(domain.getPassword());
        entity.setVerified(domain.isVerified());
        entity.setIsModerator(domain.isModerator());
        entity.setReviewAmount(domain.getReviewAmount());
        entity.setFollowerAmount(domain.getFollowerAmount());
        entity.setFollowingAmount(domain.getFollowingAmount());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }
}
```

3. `persistence/src/main/java/ar/edu/itba/paw/persistence/UserRepositoryJpa.java`
```java
import ar.edu.itba.paw.domain.user.*;
import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryJpa implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    private final UserMapper mapper;

    @Autowired
    public UserRepositoryJpa(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(UserId id) {
        UserJpaEntity entity = em.find(UserJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        String jpql = "SELECT u FROM UserJpaEntity u WHERE u.email = :email";
        List<UserJpaEntity> results = em.createQuery(jpql, UserJpaEntity.class)
            .setParameter("email", email.getValue())
            .setMaxResults(1)
            .getResultList();

        return results.isEmpty() ? Optional.empty() : Optional.of(mapper.toDomain(results.get(0)));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String jpql = "SELECT u FROM UserJpaEntity u WHERE u.username = :username";
        List<UserJpaEntity> results = em.createQuery(jpql, UserJpaEntity.class)
            .setParameter("username", username)
            .setMaxResults(1)
            .getResultList();

        return results.isEmpty() ? Optional.empty() : Optional.of(mapper.toDomain(results.get(0)));
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);

        if (user.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }

        em.flush();
        return mapper.toDomain(entity);
    }

    @Override
    public void delete(UserId id) {
        UserJpaEntity entity = em.find(UserJpaEntity.class, id.getValue());
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    public List<User> findAll(Integer page, Integer size, String orderBy, String order) {
        String jpql = "SELECT u FROM UserJpaEntity u ORDER BY u." + orderBy + " " + order;
        TypedQuery<UserJpaEntity> query = em.createQuery(jpql, UserJpaEntity.class);

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Long countAll() {
        String jpql = "SELECT COUNT(u) FROM UserJpaEntity u";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    @Override
    public void addFollower(UserId userId, UserId followerId) {
        UserJpaEntity follower = em.find(UserJpaEntity.class, followerId.getValue());
        if (follower != null) {
            if (!follower.getFollowingIds().contains(userId.getValue())) {
                follower.getFollowingIds().add(userId.getValue());
                em.merge(follower);
            }
        }
    }

    @Override
    public void removeFollower(UserId userId, UserId followerId) {
        UserJpaEntity follower = em.find(UserJpaEntity.class, followerId.getValue());
        if (follower != null) {
            follower.getFollowingIds().remove(userId.getValue());
            em.merge(follower);
        }
    }

    @Override
    public boolean isFollowing(UserId userId, UserId followedId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null && user.getFollowingIds().contains(followedId.getValue());
    }

    @Override
    public List<UserId> getFollowerIds(UserId userId, Integer page, Integer size) {
        String jpql = "SELECT u.id FROM UserJpaEntity u WHERE :userId MEMBER OF u.followingIds";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
            .setParameter("userId", userId.getValue());

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(UserId::new)
            .toList();
    }

    @Override
    public List<UserId> getFollowingIds(UserId userId, Integer page, Integer size) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user == null) return List.of();

        List<Long> followingIds = user.getFollowingIds();

        int start = (page != null && size != null) ? page * size : 0;
        int end = (size != null) ? Math.min(start + size, followingIds.size()) : followingIds.size();

        return followingIds.subList(start, end).stream()
            .map(UserId::new)
            .toList();
    }

    @Override
    public Long countFollowers(UserId userId) {
        String jpql = "SELECT COUNT(u) FROM UserJpaEntity u WHERE :userId MEMBER OF u.followingIds";
        return em.createQuery(jpql, Long.class)
            .setParameter("userId", userId.getValue())
            .getSingleResult();
    }

    @Override
    public Long countFollowing(UserId userId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null ? (long) user.getFollowingIds().size() : 0L;
    }

    @Override
    public void addFavoriteAlbum(UserId userId, Long albumId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null && !user.getFavoriteAlbumIds().contains(albumId)) {
            user.getFavoriteAlbumIds().add(albumId);
            em.merge(user);
        }
    }

    @Override
    public void removeFavoriteAlbum(UserId userId, Long albumId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null) {
            user.getFavoriteAlbumIds().remove(albumId);
            em.merge(user);
        }
    }

    @Override
    public boolean isAlbumFavorite(UserId userId, Long albumId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null && user.getFavoriteAlbumIds().contains(albumId);
    }

    @Override
    public List<Long> getFavoriteAlbumIds(UserId userId, Integer page, Integer size) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user == null) return List.of();

        List<Long> favoriteIds = user.getFavoriteAlbumIds();

        int start = (page != null && size != null) ? page * size : 0;
        int end = (size != null) ? Math.min(start + size, favoriteIds.size()) : favoriteIds.size();

        return favoriteIds.subList(start, end);
    }

    // Similar methods for Artist and Song favorites...
    @Override
    public void addFavoriteArtist(UserId userId, Long artistId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null && !user.getFavoriteArtistIds().contains(artistId)) {
            user.getFavoriteArtistIds().add(artistId);
            em.merge(user);
        }
    }

    @Override
    public void removeFavoriteArtist(UserId userId, Long artistId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null) {
            user.getFavoriteArtistIds().remove(artistId);
            em.merge(user);
        }
    }

    @Override
    public boolean isArtistFavorite(UserId userId, Long artistId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null && user.getFavoriteArtistIds().contains(artistId);
    }

    @Override
    public List<Long> getFavoriteArtistIds(UserId userId, Integer page, Integer size) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user == null) return List.of();

        List<Long> favoriteIds = user.getFavoriteArtistIds();

        int start = (page != null && size != null) ? page * size : 0;
        int end = (size != null) ? Math.min(start + size, favoriteIds.size()) : favoriteIds.size();

        return favoriteIds.subList(start, end);
    }

    @Override
    public void addFavoriteSong(UserId userId, Long songId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null && !user.getFavoriteSongIds().contains(songId)) {
            user.getFavoriteSongIds().add(songId);
            em.merge(user);
        }
    }

    @Override
    public void removeFavoriteSong(UserId userId, Long songId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null) {
            user.getFavoriteSongIds().remove(songId);
            em.merge(user);
        }
    }

    @Override
    public boolean isSongFavorite(UserId userId, Long songId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null && user.getFavoriteSongIds().contains(songId);
    }

    @Override
    public List<Long> getFavoriteSongIds(UserId userId, Integer page, Integer size) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user == null) return List.of();

        List<Long> favoriteIds = user.getFavoriteSongIds();

        int start = (page != null && size != null) ? page * size : 0;
        int end = (size != null) ? Math.min(start + size, favoriteIds.size()) : favoriteIds.size();

        return favoriteIds.subList(start, end);
    }
}
```

---

### Paso 1.3 - Application Layer (Use Cases)

**Archivos a crear**:

1. `interfaces/src/main/java/ar/edu/itba/paw/usecases/CreateUser.java`
```java
import ar.edu.itba.paw.domain.user.User;

public interface CreateUser {
    User execute(CreateUserCommand command);
}

public record CreateUserCommand(
    String username,
    String email,
    String password
) {
    public CreateUserCommand {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email required");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}
```

2. `services/src/main/java/ar/edu/itba/paw/usecases/impl/CreateUserUseCase.java`
```java
import ar.edu.itba.paw.domain.user.*;
import ar.edu.itba.paw.usecases.CreateUser;
import ar.edu.itba.paw.usecases.CreateUserCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateUserUseCase implements CreateUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User execute(CreateUserCommand command) {
        Email email = new Email(command.email());

        // Verify uniqueness
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.findByUsername(command.username()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(command.password());

        // Create aggregate
        User user = User.create(command.username(), email, hashedPassword);

        // Persist
        return userRepository.save(user);
    }
}
```

**Crear use cases adicionales**:
- `UpdateUser` / `UpdateUserUseCase`
- `DeleteUser` / `DeleteUserUseCase`
- `VerifyUser` / `VerifyUserUseCase`
- `FollowUser` / `FollowUserUseCase`
- `UnfollowUser` / `UnfollowUserUseCase`

---

### Paso 1.4 - Eliminar Código Legacy

**Archivos a ELIMINAR**:
```bash
rm models/src/main/java/ar/edu/itba/paw/models/User.java
rm interfaces/src/main/java/ar/edu/itba/paw/persistence/UserDao.java
rm persistence/src/main/java/ar/edu/itba/paw/persistence/UserJpaDao.java
rm interfaces/src/main/java/ar/edu/itba/paw/services/UserService.java
rm services/src/main/java/ar/edu/itba/paw/services/UserServiceImpl.java
```

**Archivos a ACTUALIZAR** (reemplazar inyecciones de UserService por use cases):
- `webapp/src/main/java/ar/edu/itba/paw/webapp/controller/UserController.java`
- Cualquier otro servicio que dependa de `UserService`

**Verificación**:
```bash
# Debe retornar 0 resultados:
git grep "UserDao"
git grep "UserService" | grep -v "UseCase"
git grep "models/User\.java"
```

---

### Paso 1.5 - Tests

**Tests a crear**:

1. **Unit test - Domain entity**:
```java
// models/src/test/java/ar/edu/itba/paw/domain/user/UserTest.java
import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void create_shouldValidateUsername() {
        Email email = new Email("test@example.com");

        assertThrows(IllegalArgumentException.class,
            () -> User.create("ab", email, "hashed123"));  // Too short

        assertThrows(IllegalArgumentException.class,
            () -> User.create("", email, "hashed123"));  // Blank
    }

    @Test
    public void incrementReviewCount_shouldIncrease() {
        User user = User.create("testuser", new Email("test@example.com"), "hashed123");

        assertEquals(0, user.getReviewAmount());

        user.incrementReviewCount();
        assertEquals(1, user.getReviewAmount());
    }

    @Test
    public void verify_shouldSetVerified() {
        User user = User.create("testuser", new Email("test@example.com"), "hashed123");

        assertFalse(user.isVerified());

        user.verify();
        assertTrue(user.isVerified());
    }

    @Test
    public void verify_shouldThrowIfAlreadyVerified() {
        User user = User.create("testuser", new Email("test@example.com"), "hashed123");
        user.verify();

        assertThrows(IllegalStateException.class, () -> user.verify());
    }
}
```

2. **Integration test - Repository**:
```java
// persistence/src/test/java/ar/edu/itba/paw/persistence/UserRepositoryJpaTest.java
import ar.edu.itba.paw.domain.user.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserRepositoryJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void save_shouldPersistNewUser() {
        User user = User.create("testuser", new Email("test@example.com"), "hashed123");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
    }

    @Test
    public void findByEmail_shouldReturnUser() {
        User user = User.create("testuser", new Email("test@example.com"), "hashed123");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail(new Email("test@example.com"));

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    public void findByEmail_shouldReturnEmptyIfNotFound() {
        Optional<User> found = userRepository.findByEmail(new Email("notfound@example.com"));

        assertFalse(found.isPresent());
    }
}
```

3. **Unit test - Use Case**:
```java
// services/src/test/java/ar/edu/itba/paw/usecases/impl/CreateUserUseCaseTest.java
import ar.edu.itba.paw.domain.user.*;
import ar.edu.itba.paw.usecases.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserUseCase useCase;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void execute_shouldCreateUser() {
        CreateUserCommand command = new CreateUserCommand("testuser", "test@example.com", "password123");

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return User.reconstitute(new UserId(1L), user.getUsername(), user.getEmail(),
                                    user.getPassword(), user.isVerified(), user.isModerator(),
                                    user.getReviewAmount(), user.getFollowerAmount(),
                                    user.getFollowingAmount(), user.getCreatedAt());
        });

        User result = useCase.execute(command);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void execute_shouldThrowIfEmailExists() {
        CreateUserCommand command = new CreateUserCommand("testuser", "test@example.com", "password123");

        User existingUser = User.create("existing", new Email("test@example.com"), "hashed");
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void execute_shouldThrowIfUsernameExists() {
        CreateUserCommand command = new CreateUserCommand("testuser", "test@example.com", "password123");

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        User existingUser = User.create("testuser", new Email("other@example.com"), "hashed");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        verify(userRepository, never()).save(any(User.class));
    }
}
```

---

### Paso 1.6 - Verificación de Compilación

```bash
# En la raíz del proyecto
mvn clean install

# Debe retornar: BUILD SUCCESS
```

**Si falla la compilación**:
1. Verificar que TODOS los archivos legacy fueron eliminados
2. Verificar que controllers/servicios usan use cases en vez de UserService
3. Verificar imports en todos los archivos modificados

---

### Paso 1.7 - Verificación Runtime

```bash
cd webapp
mvn jetty:run

# Test manual:
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# Verificar respuesta 201 Created
```

---

## Checklist de Finalización de User

- [ ] Domain layer creado (UserId, Email, User, UserRepository)
- [ ] Infrastructure layer creado (UserJpaEntity, UserMapper, UserRepositoryJpa)
- [ ] Application layer creado (Use cases: Create, Update, Delete, etc.)
- [ ] Código legacy eliminado (User.java, UserDao, UserJpaDao, UserService, UserServiceImpl)
- [ ] `git grep "UserDao"` → 0 resultados
- [ ] `git grep "UserService"` (excluir UseCase) → 0 resultados
- [ ] `git grep "models/User\.java"` → 0 resultados
- [ ] `mvn clean install` → BUILD SUCCESS
- [ ] Tests unitarios pasan (UserTest, CreateUserUseCaseTest)
- [ ] Tests de integración pasan (UserRepositoryJpaTest)
- [ ] API REST funcional (POST /api/users, GET /api/users/{id})
- [ ] Commit atómico realizado

---

## Fases 2-6: Mismo Patrón

**Las fases siguientes (Artist, Album, Song, Review, Comment) siguen la misma estructura**:

1. Domain Layer (Entity + ValueObjects + Repository port)
2. Infrastructure Layer (JpaEntity + Mapper + RepositoryJpa)
3. Application Layer (Use Cases)
4. Eliminar Legacy (Dao, DaoImpl, Service, ServiceImpl, Entity legacy)
5. Tests (Unit + Integration + Use Case)
6. Verificación (Compilación + Runtime)
7. Checklist + Commit

**Próxima entidad**: Artist (después de completar User 100%)

---

## Apéndice A: Diferencias con Planes Anteriores

| Aspecto | Plan Anterior (Strangler Fig) | Plan Actual (Big Bang Atómico) |
|---------|-------------------------------|-------------------------------|
| **Coexistencia** | ✅ Temporal (LegacyMapper) | ❌ No existe |
| **AlbumDao** | ✅ Coexiste con AlbumRepository | ❌ Eliminado al migrar Album |
| **Album.java legacy** | ✅ Coexiste con AlbumJpaEntity | ❌ Eliminado al migrar Album |
| **Fases completadas** | Fases 1-6 (según MEMORY.md) | NINGUNA (empezar desde cero) |
| **Orden de migración** | Por capas (EmailSender → Rating → Album) | Por entidades (User → Artist → Album) |
| **Rollback** | Revertir uso de nuevo código | Revertir commit atómico |
| **Tiempo estimado** | 18 semanas (gradual) | 25-30 horas (intensivo) |

---

## Apéndice B: Estrategia de Rollback

Si la migración de una entidad falla:

```bash
# Opción 1 - Revertir commit
git log --oneline  # Encontrar hash del commit antes de la migración
git reset --hard <hash>

# Opción 2 - Restaurar archivos manualmente
git checkout HEAD~1 -- models/src/main/java/ar/edu/itba/paw/models/User.java
git checkout HEAD~1 -- interfaces/src/main/java/ar/edu/itba/paw/persistence/UserDao.java
# ... etc
```

**Importante**: Cada entidad es un commit atómico separado, facilitando rollback granular.

---

## Apéndice C: Criterios de Éxito del Plan Completo

Al finalizar las 6 entidades:

### Técnicos
- [ ] CERO archivos en `models/src/main/java/ar/edu/itba/paw/models/` (solo en `domain/` e `infrastructure/jpa/`)
- [ ] CERO archivos Dao (solo Repository ports + adapters)
- [ ] CERO archivos Service legacy (solo Use Cases + Domain Services)
- [ ] `mvn clean install` → BUILD SUCCESS
- [ ] Todos los tests pasan (unit + integration + contract)

### Funcionales
- [ ] API REST 100% funcional (sin breaking changes)
- [ ] Frontend funciona sin modificaciones
- [ ] Performance equivalente (tolerancia +5%)
- [ ] Base de datos sin cambios de schema

### Arquitectónicos
- [ ] Domain entities sin anotaciones JPA
- [ ] Relaciones entre agregados solo por IDs
- [ ] Use cases explícitos para todas las operaciones
- [ ] Domain events para cascadas
- [ ] Puertos de infraestructura (EmailSender, DomainEventPublisher)

---

## Apéndice D: Próximos Pasos Después de Completar las 6 Entidades

Una vez completadas User, Artist, Album, Song, Review, Comment:

### Fase Post-Migración 1: Value Objects Adicionales
- Rating (validación 1-5, cálculo de promedio)
- Genre, FilterType (enums con comportamiento)

### Fase Post-Migración 2: Domain Services
- RatingService (centralizar cálculo duplicado)
- NotificationService (lógica de notificaciones)

### Fase Post-Migración 3: Domain Events
- ArtistDeleted, AlbumDeleted, SongDeleted
- Event handlers para cascadas

### Fase Post-Migración 4: Async Infrastructure
- Email async (eventos de aplicación)
- Event bus configuration

**Total estimado post-migración**: 10-15 horas adicionales

---

**FIN DEL PLAN**
