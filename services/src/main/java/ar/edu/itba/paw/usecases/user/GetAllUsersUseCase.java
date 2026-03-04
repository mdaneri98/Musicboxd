package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAllUsersUseCase implements GetAllUsers {

    private final UserRepository userRepository;

    @Autowired
    public GetAllUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> execute(String search, Integer page, Integer size) {
        if (search != null && !search.isEmpty()) {
            return userRepository.findByUsernameContaining(search, page, size);
        }
        return userRepository.findAll(page, size, "id", "asc");
    }

    @Override
    @Transactional(readOnly = true)
    public Long count() {
        return userRepository.countAll();
    }
}
