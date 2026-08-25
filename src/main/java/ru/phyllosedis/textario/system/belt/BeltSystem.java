package ru.phyllosedis.textario.system.belt;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.type.Grade;

public abstract class BeltSystem extends AbstractSystem {


    protected BeltSystem(ComponentFactoryManager cfm, ComponentManager cm, Grade grade) {
        super(cfm, cm, grade);
    }
}
