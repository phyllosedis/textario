package ru.phyllosedis.textario.logistics.inserter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.production.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.engine.ecs.component.Requires;

@Requires({InserterComponent.class, OperationFinishedMarkerComponent.class, TierMarkerComponent.class})
@Component
@Order(20)
public class InserterSystem extends AbstractSystem {
    public InserterSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        InserterComponent inserter =
                cm.get(id, InserterComponent.class);

        System.out.println(
                "Inserter " + id
                        + " transfers "
                        + inserter.getStackSize()
                        + " item(s), range="
                        + inserter.getRange()
        );

        cm.remove(
                id,
                OperationFinishedMarkerComponent.class
        );
    }
}
