package ru.phyllosedis.textario.ui;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * Держит Spring-контекст для JavaFX-приложения.
 * JavaFX создаёт свои классы сам, поэтому контекст
 * передаём через этот холдер в UiBootstrap.
 */
public final class UiContext {

    private static volatile ConfigurableApplicationContext context;

    private UiContext() {
    }

    public static void set(ConfigurableApplicationContext ctx) {
        context = ctx;
    }

    public static ConfigurableApplicationContext get() {
        ConfigurableApplicationContext ctx = context;
        if (ctx == null) {
            throw new IllegalStateException("Spring-контекст ещё не запущен");
        }
        return ctx;
    }
}
