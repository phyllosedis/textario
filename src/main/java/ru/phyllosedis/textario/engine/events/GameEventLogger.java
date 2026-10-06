package ru.phyllosedis.textario.engine.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Шина событий игры (внутрипроцессная, без Кафки).
 * Сейчас только пишет в debug-лог; сюда же позже встанут
 * ачивки, квесты или мост во внешний брокер — системы
 * при этом трогать не придётся.
 */
@Component
@Slf4j
public class GameEventLogger {

    @EventListener
    public void onBuilt(EntityBuiltEvent event) {
        log.debug("построен {} #{} на {}:{}", event.kind(), event.id(), event.x(), event.y());
    }

    @EventListener
    public void onDemolished(EntityDemolishedEvent event) {
        log.debug("снесён {} #{}", event.kind(), event.id());
    }

    @EventListener
    public void onProduced(ProducedEvent event) {
        log.debug("станция #{}: {} {} x{}", event.entityId(), event.what(), event.type(), event.count());
    }
}
