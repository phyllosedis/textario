package ru.phyllosedis.textario.system.splitter;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.SplitterComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.TransportPortComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierComponent;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.type.SplitMode;

@Requires({SplitterComponent.class, TransportPortComponent.class, TierComponent.class})
@Component
public class SplitterSystem extends AbstractSystem {

    public SplitterSystem(ComponentFactoryManager cfm, ComponentManager cm) {
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
