package ru.phyllosedis.textario.logistics.splitter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.logistics.port.LogisticPort;
import ru.phyllosedis.textario.resource.SystemOrder;

@Requires({SplitterComponent.class, LogisticPort.class})
@Component
@Order(SystemOrder.BELT)
public class SplitterSystem extends AbstractSystem {

    public SplitterSystem(ComponentFactoryRegistry cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        SplitterComponent splitter =
                cm.get(id, SplitterComponent.class);

        switch (SplitMode.UNDEFINED.getByOrdinal(splitter.getSplitMode())) {

            case ROUND_ROBIN -> {
                // выбрать следующий output
            }

            case BALANCED -> {
                // выбрать менее загруженный output
            }

            case PRIORITY_LEFT -> {
                // ...
            }

            case PRIORITY_RIGHT -> {
                // ...
            }
        }
    }
}
