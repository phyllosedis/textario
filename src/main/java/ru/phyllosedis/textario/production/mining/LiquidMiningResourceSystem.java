package ru.phyllosedis.textario.production.mining;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;
import ru.phyllosedis.textario.resource.marker.GasStateMarkerComponent;
import ru.phyllosedis.textario.resource.marker.LiquidStateMarkerComponent;

@Component
@Order(SystemOrder.MINING)
@Requires({LiquidStateMarkerComponent.class})
public class LiquidMiningResourceSystem extends MiningResourceSystem {

    public LiquidMiningResourceSystem(ComponentFactoryRegistry cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void onComplete(long id, ResourceType resType, int count) {
        super.onComplete(id, resType, count);
        System.out.println("[Добыча жидкости] станция #" + id + " тип ресурса: " + resType + " количество " + count);
    }
}
