package org.aibe4.dodeul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DodeulApplication {

    public static void main(String[] args) {
        SpringApplication.run(DodeulApplication.class, args);
    }
}
