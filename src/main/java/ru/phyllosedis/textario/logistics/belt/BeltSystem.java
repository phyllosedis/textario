package ru.phyllosedis.textario.logistics.belt;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.engine.ecs.component.Requires;

@Requires({BeltComponent.class, TierMarkerComponent.class})
@Component
@Order(30)
public class BeltSystem extends AbstractSystem {
    public BeltSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {

    }
}
