package ru.phyllosedis.textario.engine.loop;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.system.System;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Engine {
    private final List<System> systems;

    @Scheduled(fixedRateString = "${textario.tick-rate-ms}")
    public void gameTick() {
        for (System system : systems) {
            system.update();
        }
    }
}
