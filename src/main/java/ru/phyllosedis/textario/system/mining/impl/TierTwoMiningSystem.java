package ru.phyllosedis.textario.system.mining.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.tier.TierTwoMarkerComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.mining.MiningSystem;

@Order(10)
@Component
@Requires({TierTwoMarkerComponent.class})
public class TierTwoMiningSystem extends MiningSystem {
    protected TierTwoMiningSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected double getBoost() {
        return 1.5;
    }
}
