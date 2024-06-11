package ru.gb.Bank.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.Bank.model.DTO.TransferDTO;
import ru.gb.Bank.model.web.enums.StatusPay;
import ru.gb.Bank.service.security.UserService;

@RestController
@AllArgsConstructor
public class BankRestController {
    private UserService userService;

    /**
     * Метод по осуществлению оплаты (списание денежных средств у указанных держателей карты)
     * @param transferDTO
     * @return
     */
    @PostMapping("/trans")
    public ResponseEntity<StatusPay> pay(@RequestBody TransferDTO transferDTO) {
        System.out.println(transferDTO);
        StatusPay status = userService.payByNumberCard(transferDTO.getNumberReceiver(), transferDTO.getNumberSender(),
                transferDTO.getAmount());

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
