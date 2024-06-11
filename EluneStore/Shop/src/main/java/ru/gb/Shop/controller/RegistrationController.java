package ru.gb.Shop.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.gb.Shop.model.security.User;
import ru.gb.Shop.service.security.UserService;

@Controller
@AllArgsConstructor
public class RegistrationController {
    private UserService userService;

    /**
     * Метод по отображению формы для регистрации
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/registration")
    public String getPageRegistration(User user, Model model) {

        return "registration";
    }

    /**
     * Метод по получению данных от страницы и добавлению нового пользователя
     * @param user
     * @param model
     * @return
     */
    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }

        if (!userService.saveUser(user)) {
            model.addAttribute("userNameError", "Пользователь с таким именем уже существует");
            return "registration";
        }

        return "redirect:/login";
    }
}
