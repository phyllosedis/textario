package ru.phyllosedis.textario.system.inserter;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.system.AbstractSystem;

public abstract class InserterSystem extends AbstractSystem {
    public InserterSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }
}
