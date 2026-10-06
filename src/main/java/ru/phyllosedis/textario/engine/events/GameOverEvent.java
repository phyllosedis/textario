package ru.phyllosedis.textario.engine.events;

/**
 * Ядро уничтожено. Мир встаёт на паузу, дальше — load или перестройка.
 */
public record GameOverEvent(long coreId, int wave) {
}
