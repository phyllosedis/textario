package ru.phyllosedis.textario.system.mining.gas;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.state.gas.GasStateMarkerComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.mining.MiningResourceSystem;
import ru.phyllosedis.textario.type.ResourceType;

@Requires({GasStateMarkerComponent.class})
@Component
@Order(10)
public class GasMiningResourceSystem extends MiningResourceSystem {

    public GasMiningResourceSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void onComplete(long id, ResourceType resType) {
        System.out.println("[Добыча газа] Сущность " + id + " качает " + resType + " со скоростью ");
    }
}
