package ru.phyllosedis.textario;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.system.GameSystem;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GameEngine {
    private final List<GameSystem> gameSystems;
    private final ComponentFactoryManager cfm;
    private final ComponentManager cm;

    // Единственный Scheduled на ВСЮ игру. Значение берется из application.properties!
    @Scheduled(fixedRateString = "${textario.tick-rate-ms}")
    public void gameTick() {
        // По очереди вызываем каждую систему в строгом порядке
        for (GameSystem system : gameSystems) {
            system.update();
        }
    }
}
