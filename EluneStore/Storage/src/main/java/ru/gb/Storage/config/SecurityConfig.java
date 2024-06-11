package ru.gb.Storage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import ru.gb.Storage.service.security.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserService userService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/registration",
                                "/products-with-quantity", "/products-with-quantity/{id}",
                                "/rest-product/{id}", "/decrease-quantity-by-id/{id}",
                                "/return-decrease-quantity-by-id/{id}").permitAll()
                        .requestMatchers("/*", "/admin-panel/*", "/product/{id}",
                                "/delete-product/{id}", "/change-product/{id}").hasAnyRole("ADMIN"))
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/"));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }
}
