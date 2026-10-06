package ru.phyllosedis.textario.engine.events;

import ru.phyllosedis.textario.resource.ResourceType;

/**
 * Станция выдала продукцию: добыча, плавка, сборка.
 * what: mined / smelted / crafted.
 */
public record ProducedEvent(long entityId, String what, ResourceType type, int count) {
}
