package org.aibe4.dodeul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class DodeulApplication {

    public static void main(String[] args) {
        SpringApplication.run(DodeulApplication.class, args);
    }
}
