package ru.gb.Shop.service.web;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.handler.annotation.Header;
import ru.gb.Shop.model.web.Product;

@MessagingGateway(defaultRequestChannel = "textInputChanel")
public interface FileProductsGetawayInterface {
    void writeToFile(@Header(FileHeaders.FILENAME) String fileName, Product product);
}
