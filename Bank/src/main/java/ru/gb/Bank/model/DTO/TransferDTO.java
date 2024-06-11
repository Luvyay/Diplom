package ru.gb.Bank.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferDTO {
    private String numberSender;
    private String numberReceiver;
    private BigDecimal amount;
}
