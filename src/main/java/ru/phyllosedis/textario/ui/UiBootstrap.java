package ru.phyllosedis.textario.ui;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.phyllosedis.textario.bootstrap.TextarioApplication;

/**
 * Точка входа GUI. Поднимает тот же Spring-бэкенд,
 * затем отдаёт поток JavaFX-окну. Запуск:
 * mvn spring-boot:run -Dspring-boot.run.main-class=ru.phyllosedis.textario.ui.UiBootstrap
 */
public class UiBootstrap {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(TextarioApplication.class, args);
        UiContext.set(ctx);
        Application.launch(TextarioFxApp.class, args);
        int code = SpringApplication.exit(ctx);
        System.exit(code);
    }
}
