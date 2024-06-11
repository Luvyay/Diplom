package ru.gb.Bank.controller.web;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.gb.Bank.model.security.User;
import ru.gb.Bank.model.web.InfoPageUser;
import ru.gb.Bank.model.web.ChangeUser;
import ru.gb.Bank.service.security.UserService;

import java.math.BigDecimal;

@Controller
@AllArgsConstructor
public class BankController {
    private UserService userService;

    /**
     * Метод по отображению главной страницы банка
     * @return
     */
    @GetMapping("/")
    public String getHomePage() {
        return "index";
    }

    /**
     * Метод по отображению страницы профиля
     * @param model
     * @return
     */
    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User currentUser = userService.findUserByName(userDetails.getUsername());

        model.addAttribute("user", currentUser);

        return "profile";
    }

    /**
     * Метод по отображению страницы профиля администратора
     * @return
     */
    @GetMapping("/admin-panel")
    public String getAdmin() {
        return "admin-panel";
    }

    /**
     * Метод по отобраению страницы со списком пользователей
     * @param model
     * @return
     */
    @GetMapping("/admin-panel/show-users")
    public String getPageWithAllUsers(Model model) {
        return "redirect:/admin-panel/show-users/1";
    }

    /**
     * Метод по отображению конкретной страницы со списком пользователей
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/admin-panel/show-users/{idPage}")
    public String getPageWithAllUsersByIdPage(Model model, @PathVariable(name = "idPage") int id) {
        InfoPageUser infoPageUser = userService.getPageById(id);

        model.addAttribute("users", infoPageUser.getUsersInPage());
        model.addAttribute("prev", infoPageUser.getPrevPage());
        model.addAttribute("next", infoPageUser.getNextPage());

        return "admin-show-users";
    }

    /**
     * Метод по отображению страницы для редактирования пользователя
     * @param model
     * @param userForChange
     * @return
     */
    @GetMapping("/admin-panel/change-user")
    public String getPageForChangeUser(Model model, ChangeUser userForChange) {
        return "admin-change-user";
    }

    /**
     * Метод по изменению пользователя
     * @param model
     * @param changeUser
     * @return
     */
    @PostMapping("/admin-panel/change-user")
    public String changeUser(Model model, ChangeUser changeUser) {

        User userFromDb = userService.findUserById(changeUser.getId());

        if (userFromDb != null) {
            switch (changeUser.getArithOperations()) {
                // прибавление к счету пользователя
                case SUM:
                    userFromDb.setAmount(userFromDb.getAmount().add(changeUser.getAmount()));
                    userService.updateUser(userFromDb);
                    break;
                // вычитание со счета пользователя
                case DIFFERENCE:
                    userFromDb.setAmount(userFromDb.getAmount().subtract(changeUser.getAmount()));

                    // лишаем возможности администратору опускать сумму счета ниже 0
                    if (userFromDb.getAmount().compareTo(new BigDecimal(0)) < 0) {
                        userFromDb.setAmount(new BigDecimal(0));
                    }

                    userService.updateUser(userFromDb);
                    break;
            }
        }

        return "redirect:/admin-panel/change-user";
    }
}
