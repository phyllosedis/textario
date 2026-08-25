package ru.phyllosedis.textario.system.belt.impl;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.tier.TierOneMarkerComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.belt.BeltSystem;

@Requires({TierOneMarkerComponent.class})
public class TierOneBeltSystem extends BeltSystem {
    protected TierOneBeltSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected double getBoost() {
        return 1.0;
    }
}
