package ru.gb.Shop.config.web;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.gb.Shop.model.web.Product;
import ru.gb.Shop.service.security.UserService;

import java.io.File;
import java.time.LocalDateTime;

@Configuration
@AllArgsConstructor
public class IntegrationConfig {
    private UserService userService;
    @Bean
    public MessageChannel textInputChanel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel fileWriterChanel() {
        return new DirectChannel();
    }

    @Bean
    @Transformer(inputChannel = "textInputChanel", outputChannel = "fileWriterChanel")
    public GenericTransformer<Product, String> textTransformer() {
        return product -> {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();

            Long idUser = userService.findUserByName(userDetails.getUsername()).getId();

            return String.format("name: %s; price: %d; userName: %s; idUser: %s; date: %s",
                    product.getName(), product.getPrice(),
                    userDetails.getUsername(), idUser, LocalDateTime.now());
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "fileWriterChanel")
    public FileWritingMessageHandler messageHandler() {
        FileWritingMessageHandler fileWritingMessageHandler = new FileWritingMessageHandler(new File("src/main/resources"));

        fileWritingMessageHandler.setExpectReply(false);
        fileWritingMessageHandler.setFileExistsMode(FileExistsMode.APPEND);
        fileWritingMessageHandler.setAppendNewLine(true);

        return fileWritingMessageHandler;
    }
}
