package ru.phyllosedis.textario.combat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PositionComponent;

/**
 * Волны врагов. Ядра нет — песочница, тикаем молча.
 * Есть ядро — обратный отсчёт, спавн с восточного края.
 */
@Service
@Slf4j
public class WaveService {

    private final EntityBlueprintService blueprints;
    private final OccupancyGrid occupancyGrid;
    private final ComponentManager cm;

    private final int firstDelay;
    private final int interval;

    private int wave;
    private int countdown;

    public WaveService(EntityBlueprintService blueprints,
                       OccupancyGrid occupancyGrid,
                       ComponentManager cm,
                       @Value("${textario.wave.first-delay:3600}") int firstDelay,
                       @Value("${textario.wave.interval:2400}") int interval) {
        this.blueprints = blueprints;
        this.occupancyGrid = occupancyGrid;
        this.cm = cm;
        this.firstDelay = firstDelay;
        this.interval = interval;
        this.wave = 0;
        this.countdown = firstDelay;
    }

    public void tick() {
        Long coreId = cm.entitiesWith(CoreMarkerComponent.class).stream().findFirst().orElse(null);
        if (coreId == null) {
            return;
        }
        if (--countdown == 600) {
            log.info("Волна {} через 10 секунд. Готовь турели.", wave + 1);
        }
        if (countdown > 0) {
            return;
        }
        launchWave();
        countdown = interval;
    }

    public void forceWave() {
        launchWave();
        countdown = interval;
    }

    public int currentWave() {
        return wave;
    }

    public String status() {
        boolean sandbox = cm.entitiesWith(CoreMarkerComponent.class).isEmpty();
        if (sandbox) {
            return "ядра нет — песочница, волн не будет";
        }
        return "волна " + wave + ", следующая через " + Math.max(0, countdown / 100) + "с";
    }

    private void launchWave() {
        wave++;
        Long coreId = cm.entitiesWith(CoreMarkerComponent.class).stream().findFirst().orElse(null);
        if (coreId == null) {
            wave--;
            return;
        }
        PositionComponent corePos = cm.get(coreId, PositionComponent.class);
        int count = Math.min(2 + wave, 20);
        int spawned = 0;
        for (int i = 0; i < count; i++) {
            int x = occupancyGrid.getWidth() - 3;
            int y = corePos == null ? occupancyGrid.getHeight() / 2 : corePos.getY() - count / 2 + i;
            try {
                blueprints.spawnEnemy(x, y, wave);
                spawned++;
            } catch (Exception e) {
                log.debug("не заспавнил на {}:{}: {}", x, y, e.getMessage());
            }
        }
        log.info("Волна {}! Врагов: {}", wave, spawned);
    }
}
