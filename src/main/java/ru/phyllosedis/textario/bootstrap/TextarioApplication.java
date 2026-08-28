package ru.phyllosedis.textario.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"ru.phyllosedis.textario"})
public class TextarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextarioApplication.class, args);
    }

}
