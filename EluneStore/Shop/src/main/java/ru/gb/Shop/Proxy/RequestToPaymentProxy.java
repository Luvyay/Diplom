package ru.gb.Shop.Proxy;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.gb.Shop.model.web.enums.StatusPay;

import java.math.BigDecimal;

@FeignClient(name = "payment", url = "http://localhost:8765")
public interface RequestToPaymentProxy {
    @PostMapping("/payment/{numberOfCard}/{amount}")
    StatusPay toDoPay(@PathVariable(name = "numberOfCard") String numberOfCard,
                      @PathVariable(name = "amount") BigDecimal amount);
}
