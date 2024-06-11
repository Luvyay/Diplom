package ru.gb.Storage.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    /**
     * Метод по отображению страницы входа
     * @return
     */
    @GetMapping("/login")
    public String getPageLogin() {
        return "login";
    }
}
