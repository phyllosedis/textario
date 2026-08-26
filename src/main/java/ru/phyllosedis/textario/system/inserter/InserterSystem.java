package ru.phyllosedis.textario.system.inserter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.InserterComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierComponent;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.system.Requires;

@Requires({InserterComponent.class, OperationFinishedMarkerComponent.class, TierComponent.class})
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
