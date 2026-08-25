package ru.phyllosedis.textario;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.system.GameSystem;

import java.util.List;

@Component
public class GameEngine {
    private final List<GameSystem> gameSystems;

    public GameEngine(List<GameSystem> gameSystems) {
        this.gameSystems = gameSystems;
    }

    // Единственный Scheduled на ВСЮ игру. Значение берется из application.properties!
    @Scheduled(fixedRateString = "${textario.tick-rate-ms}")
    public void gameTick() {
        // По очереди вызываем каждую систему в строгом порядке
        for (GameSystem system : gameSystems) {
            system.update();
        }
    }
}
