package ru.phyllosedis.textario.system;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.type.Grade;

public abstract class AbstractSystem implements GameSystem {
    protected final ComponentFactoryManager cfm;
    protected final ComponentManager cm;
    protected final Grade grade;

    protected AbstractSystem(ComponentFactoryManager cfm, ComponentManager cm, Grade grade) {
        this.cfm = cfm;
        this.cm = cm;
        this.grade = grade;
    }
}
