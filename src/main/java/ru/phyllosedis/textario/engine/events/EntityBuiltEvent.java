package ru.phyllosedis.textario.engine.events;

/**
 * Постройка создана (включая загрузку из сейва).
 */
public record EntityBuiltEvent(long id, String kind, int x, int y) {
}
