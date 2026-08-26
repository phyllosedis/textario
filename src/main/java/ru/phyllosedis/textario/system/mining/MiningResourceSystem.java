package ru.phyllosedis.textario.system.mining;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierComponent;
import ru.phyllosedis.textario.component.impl.meta.station.product.DispatchedProductComponent;
import ru.phyllosedis.textario.component.impl.mining.MiningComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.station.StationProgressSystem;
import ru.phyllosedis.textario.type.ResourceType;

@Requires({MiningComponent.class, OperationFinishedMarkerComponent.class, TierComponent.class})
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
}
