package ru.gb.Bank.service.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.Bank.model.security.User;
import ru.gb.Bank.model.web.InfoPageUser;
import ru.gb.Bank.model.web.enums.StatusPay;
import ru.gb.Bank.repository.security.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @PersistenceContext
    private EntityManager em;
    private static final int countUserOnPage = 5;

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
        user.setAmount(new BigDecimal(0));
        userRepository.save(user);

        // Вытягиваем только что созданного пользователя и указываем для него numberOfCard равный id
        // после чего пересохраняем
        userFromDb = userRepository.findByUsername(user.getUsername());
        userFromDb.setNumberOfCard(String.valueOf(userFromDb.getId()));
        userRepository.save(userFromDb);

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
     * Метод по получению информации о 1 странице
     * @return
     */
    public InfoPageUser getPage() {
        return getPageById(1);
    }

    /**
     * Метод по получению информации о странице по idPage
     * @param id
     * @return
     */
    public InfoPageUser getPageById(int id) {
        // получение числа страниц
        int countPage = (int) Math.ceil((double) allUsers().size() / countUserOnPage);

        if (countPage == 0) countPage = 1;

        if (id > countPage) id = countPage;
        if (id < 0) id = 1;

        Integer nextPage = id + 1;

        if (nextPage > countPage) nextPage = null;

        Integer prevPage = id - 1;

        if (prevPage <= 0) prevPage = null;

        int limitValue = countUserOnPage;
        int offsetValue = (id - 1) * limitValue;

        List<User> users = em.createQuery("SELECT u FROM User u ORDER BY u.id ASC LIMIT :paramLimit OFFSET :paramOffset",
                        User.class)
                .setParameter("paramLimit", limitValue)
                .setParameter("paramOffset", offsetValue)
                .getResultList();

        return new InfoPageUser(countPage, prevPage, nextPage, users);
    }

    /**
     * Метод поиска пользователя по номеру карты
     * @param number
     * @return
     */
    public User findUserByNumberCard(String number) {
        List<User> user = em.createQuery("SELECT u FROM User u WHERE u.numberOfCard = :number",
                        User.class)
                .setParameter("number", number)
                .getResultList();

        if (user.size() == 0) return null;

        return user.get(0);
    }

    /**
     * Метод по осуществлению перевода от одного юзера другому по средствам указания
     * номера карты получателя, номера карты отправителя и суммы перевода
     * @param numberReceiver
     * @param numberSender
     * @param amount
     * @return
     */
    @Transactional
    public StatusPay payByNumberCard(String numberReceiver, String numberSender, BigDecimal amount) {
        User receiver = findUserByNumberCard(numberReceiver);
        User sender = findUserByNumberCard(numberSender);

        // пользователь не найден
        if (sender == null || receiver == null) {
            return StatusPay.USER_NOT_FOUND;
        }

        // недостаточно денег для перевода
        if (sender.getAmount().compareTo(amount) < 0) {
            return StatusPay.NOT_ENOUGH_MONEY;
        }

        sender.setAmount(sender.getAmount().subtract(amount));
        receiver.setAmount(receiver.getAmount().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        return StatusPay.OK;
    }

    /**
     * Специальный метод для изменения роли пользователя на администратора (конкретно пользователя с айди 1)
     */
    public void createAdmin() {
        User userFromDb = userRepository.findById(1L).orElse(null);
        userFromDb.setAuthority("ROLE_ADMIN");
        userRepository.save(userFromDb);
    }
}
