package com.civicfix;

import com.civicfix.model.User;
import com.civicfix.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CivicFixApplication {

    public static void main(String[] args) {
        SpringApplication.run(CivicFixApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@civicfix.com").isEmpty()) {
                User admin = new User();
                admin.setName("Administrator");
                admin.setEmail("admin@civicfix.com");
                admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
                admin.setPhone("");
                admin.setRole("ADMIN");
                admin.setPoints(0);
                userRepository.save(admin);
                System.out.println("Seed: Admin user created (admin@civicfix.com / admin123)");
            }
        };
    }
}
