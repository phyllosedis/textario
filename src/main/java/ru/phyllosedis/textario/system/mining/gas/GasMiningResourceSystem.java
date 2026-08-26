package ru.phyllosedis.textario.system.mining.gas;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.state.gas.GasStateMarkerComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.mining.MiningResourceSystem;
import ru.phyllosedis.textario.type.ResourceType;

@Requires({GasStateMarkerComponent.class})
public class GasMiningResourceSystem extends MiningResourceSystem {

    public GasMiningResourceSystem(ComponentFactoryManager cfm, ComponentManager cm, double boost) {
        super(cfm, cm, boost);
    }

    @Override
    protected void mine(long id, ResourceType resType) {
        System.out.println("[Добыча газа] Сущность " + id + " качает " + resType + " со скоростью " + boost);
    }
}
