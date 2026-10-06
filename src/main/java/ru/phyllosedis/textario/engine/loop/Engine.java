package ru.phyllosedis.textario.engine.loop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.system.System;
import ru.phyllosedis.textario.engine.metrics.MetricsService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Engine {
    private final List<System> systems;
    private final TickGate tickGate;
    private final MetricsService metrics;

    @Scheduled(fixedRateString = "${textario.tick-rate-ms}")
    public void gameTick() {
        if (tickGate.isPaused()) {
            return;
        }
        long tickNo = metrics.nextTick();
        MDC.put("tick", String.valueOf(tickNo));
        try {
            Map<String, Long> timings = new LinkedHashMap<>();
            long start = java.lang.System.nanoTime();
            for (System system : systems) {
                long systemStart = java.lang.System.nanoTime();
                try {
                    system.update();
                } catch (Exception e) {
                    log.error("[Engine] система {} упала на тике: {}",
                            system.getClass().getSimpleName(), e.getMessage());
                }
                timings.put(system.getClass().getSimpleName(),
                        java.lang.System.nanoTime() - systemStart);
            }
            metrics.tickDone(timings, java.lang.System.nanoTime() - start);
        } finally {
            MDC.remove("tick");
        }
    }
}
