package ru.gb.Bank.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.gb.Bank.model.web.enums.ArithOperations;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ChangeUser {
    private Long id;
    private ArithOperations arithOperations;
    private BigDecimal amount;
}
