package ru.phyllosedis.textario.system.mining.solid;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.state.solid.SolidStateMarkerComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.mining.MiningResourceSystem;
import ru.phyllosedis.textario.type.ResourceType;

@Requires({SolidStateMarkerComponent.class})
@Component
@Order(10)
public class SolidMiningResourceSystem extends MiningResourceSystem {

    public SolidMiningResourceSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void onComplete(long id, ResourceType resType) {

        System.out.println(
                "Miner " + id + " mined " + resType
        );
    }
}
