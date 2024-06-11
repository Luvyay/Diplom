package ru.gb.Storage.service.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gb.Storage.model.security.User;
import ru.gb.Storage.repository.security.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    /**
     * Метод по поиску пользователя по id
     * @param userId
     * @return
     */
    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);

        return userFromDb.orElseThrow();
    }

    /**
     * Метод по поиску пользователя по имени
     * @param name
     * @return
     */
    public User findUserByName(String name) {
        return userRepository.findByUsername(name);
    }

    /**
     * Метод по получению списка всех пользователей
     * @return
     */
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    /**
     * Метод для добавления нового пользователя
     * @param user
     * @return
     */
    public boolean saveUser(User user) {
        User userFromDb = userRepository.findByUsername(user.getUsername());

        // если такой пользователь есть (нашелся по имени), то возвращаем false
        if (userFromDb != null) {
            return false;
        }

        user.setAuthority("ROLE_USER");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return true;
    }

    /**
     * Метод по обновлению информации пользователя
     * @param user
     */
    public void updateUser(User user) {
        userRepository.save(user);
    }

    /**
     * Метод по удалению пользователя по id
     * @param userId
     * @return
     */
    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);

            return true;
        }

        return false;
    }

    /**
     * Специальный метод для изменения роли пользователя на администратора (конкретно пользователя с айди 1)
     */
    public void createAdmin() {
        User userFromDb = userRepository.findById(1L).orElse(null);
        userFromDb.setAuthority("ROLE_ADMIN");
        updateUser(userFromDb);
    }
}
