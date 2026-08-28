package ru.phyllosedis.textario.production.mining;

import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.logistics.ContentStateComponent;
import ru.phyllosedis.textario.production.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.world.PositionComponent;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.system.StationProgressSystem;
import ru.phyllosedis.textario.resource.ResourceType;

@Requires({
        MiningComponent.class,
        OperationFinishedMarkerComponent.class,
        TierMarkerComponent.class,
        ContentStateComponent.class,
        PositionComponent.class,
        ExtractionComponent.class
})
public abstract class MiningResourceSystem extends StationProgressSystem {

    public MiningResourceSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        MiningComponent mining = cm.get(id, MiningComponent.class);
        ResourceType resType = ResourceType.UNDEFINED.getByOrdinal(mining.getResourceType());

        onComplete(id, resType);

        // Снимаем маркер завершения работы станции
        cm.remove(id, OperationFinishedMarkerComponent.class);

        cm.add(id, cfm.create(new DispatchedProductComponent.Args(resType, 1))); // сигнал для LogisticsSystem, передаём манипулятору или трубе
    }

    protected abstract void onComplete(long id, ResourceType resType);

    @Override
    protected double getSpeed(long id) {
        return cm.get(id, ExtractionComponent.class).getSpeed();
    }
}
