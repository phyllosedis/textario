package ru.phyllosedis.textario.system.belt;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.system.AbstractSystem;

public abstract class BeltSystem extends AbstractSystem {
    protected BeltSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {

    }
}
