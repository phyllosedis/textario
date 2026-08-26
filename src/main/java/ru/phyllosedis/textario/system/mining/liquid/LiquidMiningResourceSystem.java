package ru.phyllosedis.textario.system.mining.liquid;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.state.liquid.LiquidStateMarkerComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.mining.MiningResourceSystem;
import ru.phyllosedis.textario.type.ResourceType;

@Requires({LiquidStateMarkerComponent.class})
@Component
@Order(10)
public class LiquidMiningResourceSystem extends MiningResourceSystem {

    public LiquidMiningResourceSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void onComplete(long id, ResourceType resType) {
        System.out.println("[Жидкая добыча] Сущность " + id + " качает " + resType + " со скоростью ");
    }

}
