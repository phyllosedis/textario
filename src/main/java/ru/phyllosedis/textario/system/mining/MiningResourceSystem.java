package ru.phyllosedis.textario.system.mining;

import lombok.Getter;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.station.StationComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.component.impl.mining.MiningComponent;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.type.ResourceType;

@Requires({MiningComponent.class, OperationFinishedMarkerComponent.class, StationComponent.class})
public abstract class MiningResourceSystem extends AbstractSystem {

    @Getter
    protected final double boost;

    public MiningResourceSystem(ComponentFactoryManager cfm, ComponentManager cm, double boost) {
        super(cfm, cm);
        this.boost = boost;
    }

    @Override
    protected void updateEntity(long id) {
        MiningComponent mining = cm.get(id, MiningComponent.class);
        ResourceType resType = ResourceType.UNDEFINED.getByOrdinal(mining.getResourceType());

        mine(id, resType);

        // Снимаем маркер завершения работы станции
        cm.remove(id, OperationFinishedMarkerComponent.class);
    }

    protected abstract void mine(long id, ResourceType resType);
}
