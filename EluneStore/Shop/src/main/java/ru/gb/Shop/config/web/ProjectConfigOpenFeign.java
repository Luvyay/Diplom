package ru.gb.Shop.config.web;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ru.gb.Shop.proxy")
public class ProjectConfigOpenFeign {
}
