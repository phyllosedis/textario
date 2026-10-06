package ru.phyllosedis.textario.engine.loop;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Пауза симуляции для UI/отладки. Фронтенд дёргает этот бин,
 * движок проверяет его каждый тик. Системы ничего о нём не знают.
 */
@Component
public class TickGate {

    private final AtomicBoolean paused = new AtomicBoolean(false);

    public boolean isPaused() {
        return paused.get();
    }

    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    public boolean toggle() {
        return paused.getAndSet(!paused.get());
    }
}
