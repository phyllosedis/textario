package ru.phyllosedis.textario.production.mining;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.engine.events.ProducedEvent;
import ru.phyllosedis.textario.engine.metrics.MetricsService;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.production.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.world.PositionComponent;

@Requires({
        MiningComponent.class,
        OperationFinishedMarkerComponent.class,
        TierMarkerComponent.class,
        PositionComponent.class,
        InventoryComponent.class
})
@Slf4j
public abstract class MiningResourceSystem extends AbstractSystem {

    protected final MetricsService metrics;
    protected final ApplicationEventPublisher publisher;

    public MiningResourceSystem(ComponentFactoryRegistry cfm, ComponentManager cm,
                                MetricsService metrics, ApplicationEventPublisher publisher) {
        super(cfm, cm);
        this.metrics = metrics;
        this.publisher = publisher;
    }

    @Override
    protected void updateEntity(long id) {
        MiningComponent mining = cm.get(id, MiningComponent.class);
        OperationFinishedMarkerComponent finished = cm.get(id, OperationFinishedMarkerComponent.class);
        if (mining == null || finished == null) {
            return;
        }
        ResourceType resType = ResourceType.UNDEFINED.getByOrdinal(mining.getResourceType());
        int count = finished.getCompletedCycles();

        // Складываем добытое в выходной буфер, сброс прогресса, снятие флага
        cm.add(id, cfm.create(new DispatchedProductComponent.Args(resType, count)));
        cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));
        cm.remove(id, OperationFinishedMarkerComponent.class);

        metrics.count("mined", resType, count);
        publisher.publishEvent(new ProducedEvent(id, "mined", resType, count));

        onComplete(id, resType, count);
    }

    protected void onComplete(long id, ResourceType resType, int count) {
        log.debug("Станция {} завершила добычу (кол-во предметов: {}) тип предмета {} агрегатное состояние предмета {}",
                id, count, resType, resType.getState());
    }

}
