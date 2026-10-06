package ru.phyllosedis.textario.combat;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.resource.SystemOrder;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PositionComponent;

import java.util.Comparator;

/**
 * Враг ползёт к ближайшему ядру (жадно по Манхэттену).
 * Правила v1: бьёт только то, у чего есть HP (ядро, турели),
 * сквозь остальное просачивается. Ядра нет — стоит на месте.
 */
@Requires({EnemyMarkerComponent.class, EnemyComponent.class, PositionComponent.class, ProgressComponent.class})
@Component
@Order(SystemOrder.ENEMY)
public class EnemySystem extends AbstractSystem {

    private final OccupancyGrid occupancyGrid;

    public EnemySystem(ComponentFactoryRegistry cfm, ComponentManager cm, OccupancyGrid occupancyGrid) {
        super(cfm, cm);
        this.occupancyGrid = occupancyGrid;
    }

    @Override
    protected void updateEntity(long id) {
        EnemyComponent enemy = cm.get(id, EnemyComponent.class);
        PositionComponent position = cm.get(id, PositionComponent.class);
        ProgressComponent progress = cm.get(id, ProgressComponent.class);
        if (enemy == null || position == null || progress == null) {
            return;
        }

        double moved = progress.getProgress() + enemy.getSpeed();
        if (moved < 100.0) {
            cm.add(id, cfm.create(new ProgressComponent.Args(moved)));
            return;
        }
        cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));

        Long coreId = nearestCore(position);
        if (coreId == null) {
            return;
        }
        PositionComponent corePos = cm.get(coreId, PositionComponent.class);
        if (corePos == null) {
            return;
        }

        int dx = Integer.compare(corePos.getX() - position.getX(), 0);
        int dy = Integer.compare(corePos.getY() - position.getY(), 0);
        int nx = position.getX() + (Math.abs(corePos.getX() - position.getX()) >= Math.abs(corePos.getY() - position.getY()) ? dx : 0);
        int ny = position.getY() + (Math.abs(corePos.getX() - position.getX()) >= Math.abs(corePos.getY() - position.getY()) ? 0 : dy);
        if (nx == position.getX() && ny == position.getY()) {
            nx = position.getX() + dx;
            ny = position.getY() + dy;
        }

        Long occupant = occupancyGrid.getEntityAt(nx, ny);
        if (occupant != null && occupant != id) {
            HealthComponent target = cm.get(occupant, HealthComponent.class);
            if (target != null) {
                hit(occupant, target, enemy.getDamage());
            }
            return;
        }
        if (occupant != null) {
            return;
        }

        occupancyGrid.freeCell(position.getX(), position.getY());
        occupancyGrid.occupyCell(nx, ny, id);
        cm.add(id, cfm.create(new PositionComponent.Args(nx, ny)));
    }

    private Long nearestCore(PositionComponent from) {
        return cm.entitiesWith(CoreMarkerComponent.class).stream()
                .min(Comparator.comparingInt(id -> {
                    PositionComponent p = cm.get(id, PositionComponent.class);
                    if (p == null) {
                        return Integer.MAX_VALUE;
                    }
                    return Math.abs(p.getX() - from.getX()) + Math.abs(p.getY() - from.getY());
                }))
                .orElse(null);
    }

    private void hit(long targetId, HealthComponent target, int damage) {
        int hp = target.getHp() - damage;
        if (hp <= 0 && !cm.has(targetId, CoreMarkerComponent.class)) {
            destroy(targetId);
            return;
        }
        cm.add(targetId, cfm.create(
                new HealthComponent.Args(hp, target.getMaxHp())));
    }

    private void destroy(long targetId) {
        PositionComponent pos = cm.get(targetId, PositionComponent.class);
        BuildingComponent building = cm.get(targetId, BuildingComponent.class);
        if (pos != null && building != null) {
            for (int x = pos.getX(); x < pos.getX() + building.getWidth(); x++) {
                for (int y = pos.getY(); y < pos.getY() + building.getHeight(); y++) {
                    occupancyGrid.freeCell(x, y);
                }
            }
        }
        cm.removeEntity(targetId);
    }
}
