package ru.phyllosedis.textario.logistics.belt;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.resource.SystemOrder;

@Requires({BeltComponent.class})
@Component
@Order(SystemOrder.BELT)
public class BeltSystem extends AbstractSystem {
    public BeltSystem(ComponentFactoryRegistry cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {

    }
}
