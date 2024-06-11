package ru.gb.Payment.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ru.gb.Payment.proxy")
public class ProjectConfigOpenFeign {
}
