package ru.phyllosedis.textario.production.mining.solid;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.production.mining.MiningResourceSystem;
import ru.phyllosedis.textario.resource.ResourceType;

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
