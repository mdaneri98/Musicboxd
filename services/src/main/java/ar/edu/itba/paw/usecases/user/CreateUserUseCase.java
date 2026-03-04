package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.Email;
import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserRepository;
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

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.findByUsername(command.username()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        String hashedPassword = passwordEncoder.encode(command.password());

        User user = User.create(command.username(), email, hashedPassword);

        return userRepository.save(user);
    }
}
