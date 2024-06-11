package ru.gb.Payment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.Payment.aspect.TrackUserAction;
import ru.gb.Payment.model.DTO.TransferDTO;
import ru.gb.Payment.model.enums.StatusPay;
import ru.gb.Payment.proxy.RequestForPayProxy;

import java.math.BigDecimal;

@RestController
@AllArgsConstructor
public class PaymentRestController {
    private RequestForPayProxy requestForPayProxy;
    // Номер карты получателя (т.е. владельца магазина)
    private static final String NUMBER_OF_CARD_RECEIVER = "2";

    /**
     * Метод по совершению запроса в банк для списания денежных средств
     * @param numberOfCard
     * @param amount
     * @return
     */
    @PostMapping("/payment/{numberOfCard}/{amount}")
    @TrackUserAction
    public ResponseEntity<StatusPay> toDoPay(@PathVariable(name = "numberOfCard") String numberOfCard,
                                             @PathVariable(name = "amount")BigDecimal amount) {
        StatusPay statusPay = requestForPayProxy.requestForPayToBank(new TransferDTO(numberOfCard,
                NUMBER_OF_CARD_RECEIVER, amount));

        return ResponseEntity.status(HttpStatus.OK).body(statusPay);
    }
}
