package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.User;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    /*
        FIXME: Add required `business logic`
     */

    private final UserDao userDao;
    public UserServiceImpl(final UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public int save(User user) {
        return userDao.save(user);
    }

    @Override
    public int update(User user) {
        return userDao.update(user);
    }

    @Override
    public int deleteById(long id) {
        return userDao.deleteById(id);
    }
}
