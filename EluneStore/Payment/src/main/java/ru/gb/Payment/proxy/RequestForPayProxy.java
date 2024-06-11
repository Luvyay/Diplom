package ru.gb.Payment.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.gb.Payment.model.DTO.TransferDTO;
import ru.gb.Payment.model.enums.StatusPay;

@FeignClient(name = "requestForPay", url = "http://localhost:8081")
public interface RequestForPayProxy {
    @PostMapping("/trans")
    StatusPay requestForPayToBank(@RequestBody TransferDTO transferDTO);
}
