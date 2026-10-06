package ru.phyllosedis.textario.production.mining;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;
import ru.phyllosedis.textario.resource.marker.GasStateMarkerComponent;
import ru.phyllosedis.textario.resource.marker.SolidStateMarkerComponent;

@Component
@Order(SystemOrder.MINING)
@Requires({SolidStateMarkerComponent.class})
@Slf4j
public class SolidMiningResourceSystem extends MiningResourceSystem {

    public SolidMiningResourceSystem(ComponentFactoryRegistry cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void onComplete(long id, ResourceType resType, int count) {
        super.onComplete(id, resType, count);
        log.debug("[Добыча твёрдого предмета] станция #{} тип ресурса: {} количество {}", id, resType, count);
    }
}