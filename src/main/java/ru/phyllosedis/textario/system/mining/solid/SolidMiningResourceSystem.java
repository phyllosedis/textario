package ru.phyllosedis.textario.system.mining.solid;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.state.solid.SolidStateMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.station.product.DispatchedProductComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.mining.MiningResourceSystem;
import ru.phyllosedis.textario.system.tier.TieredSystem;
import ru.phyllosedis.textario.type.ResourceType;

@Requires({SolidStateMarkerComponent.class})
public abstract class SolidMiningResourceSystem extends MiningResourceSystem implements TieredSystem {

    public SolidMiningResourceSystem(ComponentFactoryManager cfm, ComponentManager cm, double boost) {
        super(cfm, cm, boost);
    }

    @Override
    protected void mine(long id, ResourceType resType) {
        cm.add(id, cfm.create(new DispatchedProductComponent.Args(resType, 1)));
    }
}
