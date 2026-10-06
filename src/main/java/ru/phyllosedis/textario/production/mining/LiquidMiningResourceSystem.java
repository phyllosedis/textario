package ru.phyllosedis.textario.production.mining;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.metrics.MetricsService;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;
import ru.phyllosedis.textario.resource.marker.GasStateMarkerComponent;
import ru.phyllosedis.textario.resource.marker.LiquidStateMarkerComponent;

@Component
@Order(SystemOrder.MINING)
@Requires({LiquidStateMarkerComponent.class})
@Slf4j
public class LiquidMiningResourceSystem extends MiningResourceSystem {

    public LiquidMiningResourceSystem(ComponentFactoryRegistry cfm, ComponentManager cm,
                                      MetricsService metrics, ApplicationEventPublisher publisher) {
        super(cfm, cm, metrics, publisher);
    }

    @Override
    protected void onComplete(long id, ResourceType resType, int count) {
        super.onComplete(id, resType, count);
        log.debug("[Добыча жидкости] станция #{} тип ресурса: {} количество {}", id, resType, count);
    }
}
