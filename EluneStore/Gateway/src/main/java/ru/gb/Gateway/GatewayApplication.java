package ru.gb.Gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("Storage", r -> r.path("/products-with-quantity", "/products-with-quantity/{id}", "/rest-product/{id}", "/decrease-quantity-by-id/{id}", "/return-decrease-quantity-by-id/{id}")
						.uri("http://localhost:8079/"))
				.route("Payment", r -> r.path("/payment/{numberOfCard}/{amount}")
						.uri("http://localhost:8078/"))
				.build();
	}
}
