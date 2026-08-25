package ru.phyllosedis.textario.system.mining;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.type.Grade;

public abstract class MiningSystem extends AbstractSystem {

    protected MiningSystem(ComponentFactoryManager cfm, ComponentManager cm, Grade grade) {
        super(cfm, cm, grade);
    }

    @Override
    abstract public void update();
}
