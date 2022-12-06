package bci.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@ComponentScan(basePackageClasses = {BciChallengeApplication.class, Jsr310JpaConverters.class})
public class BciChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BciChallengeApplication.class, args);
    }

}
