package ru.phyllosedis.textario.system.belt.impl.tierone;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.system.belt.BeltSystem;
import ru.phyllosedis.textario.type.Grade;

public class TierOneBeltSystem extends BeltSystem {


    protected TierOneBeltSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm, Grade.TIER_1);
    }

    @Override
    public void update() {

    }
}
