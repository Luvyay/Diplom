package ru.gb.Bank.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.gb.Bank.model.security.User;

import java.util.List;

@Data
@AllArgsConstructor
public class InfoPageUser {
    private int countPage;
    private Integer prevPage;
    private Integer nextPage;
    private List<User> usersInPage;
}
