package ru.phyllosedis.textario.combat;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.engine.metrics.MetricsService;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.inventory.InventoryAccess;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PositionComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Requires({TurretComponent.class, PositionComponent.class, BuildingComponent.class,
        InventoryComponent.class, ProgressComponent.class})
@Component
@Order(SystemOrder.TURRET)
public class TurretSystem extends AbstractSystem {

    private final OccupancyGrid occupancyGrid;
    private final MetricsService metrics;

    public TurretSystem(ComponentFactoryRegistry cfm, ComponentManager cm,
                        OccupancyGrid occupancyGrid, MetricsService metrics) {
        super(cfm, cm);
        this.occupancyGrid = occupancyGrid;
        this.metrics = metrics;
    }

    @Override
    protected void updateEntity(long id) {
        TurretComponent turret = cm.get(id, TurretComponent.class);
        PositionComponent position = cm.get(id, PositionComponent.class);
        ProgressComponent progress = cm.get(id, ProgressComponent.class);
        InventoryComponent ammo = cm.get(id, InventoryComponent.class);
        if (turret == null || position == null || progress == null || ammo == null) {
            return;
        }

        double ready = progress.getProgress() + 100.0 / turret.getInterval();
        if (ready < 100.0) {
            cm.add(id, cfm.create(new ProgressComponent.Args(ready)));
            return;
        }

        Long target = nearestEnemy(position, turret.getRange());
        ResourceType ammoType = ResourceType.UNDEFINED.getByOrdinal(turret.getAmmo());
        if (target == null || !hasAmmo(ammo, ammoType)) {
            cm.add(id, cfm.create(new ProgressComponent.Args(100.0)));
            return;
        }

        consumeAmmo(id, ammo, ammoType);
        HealthComponent health = cm.get(target, HealthComponent.class);
        if (health == null) {
            cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));
            return;
        }
        int left = health.getHp() - turret.getDamage();
        if (left <= 0) {
            destroy(target);
            metrics.countRaw("kills:enemy", 1);
        } else {
            cm.add(target, cfm.create(new HealthComponent.Args(left, health.getMaxHp())));
        }
        cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));
    }

    private Long nearestEnemy(PositionComponent from, int range) {
        return cm.entitiesWith(EnemyMarkerComponent.class).stream()
                .filter(id -> {
                    PositionComponent p = cm.get(id, PositionComponent.class);
                    return p != null
                            && Math.abs(p.getX() - from.getX()) + Math.abs(p.getY() - from.getY()) <= range;
                })
                .min(Comparator.comparingInt(id -> {
                    PositionComponent p = cm.get(id, PositionComponent.class);
                    return Math.abs(p.getX() - from.getX()) + Math.abs(p.getY() - from.getY());
                }))
                .orElse(null);
    }

    private boolean hasAmmo(InventoryComponent ammo, ResourceType type) {
        return ammo.getSlots().stream()
                .anyMatch(s -> s.resource() == type.ordinal() && s.count() > 0);
    }

    private void consumeAmmo(long id, InventoryComponent ammo, ResourceType type) {
        List<InventoryComponent.Slot> slots = new ArrayList<>();
        boolean taken = false;
        for (InventoryComponent.Slot s : ammo.getSlots()) {
            if (!taken && s.resource() == type.ordinal() && s.count() > 0) {
                taken = true;
                if (s.count() > 1) {
                    slots.add(new InventoryComponent.Slot(s.resource(), s.count() - 1));
                }
            } else {
                slots.add(s);
            }
        }
        InventoryAccess.writeInventory(cm, cfm, id, ammo, slots);
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
