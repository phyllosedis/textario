package ru.phyllosedis.textario.system.mining.liquid;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.state.liquid.LiquidStateMarkerComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.mining.MiningResourceSystem;
import ru.phyllosedis.textario.type.ResourceType;

@Requires({LiquidStateMarkerComponent.class})
public class LiquidMiningResourceSystem extends MiningResourceSystem {

    public LiquidMiningResourceSystem(ComponentFactoryManager cfm, ComponentManager cm, double boost) {
        super(cfm, cm, boost);
    }

    @Override
    protected void mine(long id, ResourceType resType) {
        System.out.println("[Жидкая добыча] Сущность " + id + " качает " + resType + " со скоростью " + boost);
    }

}
