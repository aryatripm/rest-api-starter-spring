package id.arya.portofolio.users;

import id.arya.portofolio.users.auth.RegisterRequest;
import id.arya.portofolio.users.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static id.arya.portofolio.users.user.Role.ADMIN;

@SpringBootApplication
public class UsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(UserService service) {
        return args -> {
            var admin = RegisterRequest.builder()
                    .username("admin")
                    .email("admin@mail.com")
                    .password("password")
                    .role(ADMIN)
                    .build();
            service.register(admin);
            System.out.println("ADMIN CREDENTIAL CREATED: admin:password");
        };
    }

}
