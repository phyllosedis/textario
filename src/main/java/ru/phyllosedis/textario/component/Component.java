package ru.phyllosedis.textario.component;

import ru.phyllosedis.textario.type.ComponentType;

public abstract class Component {
    public final ComponentType componentType;

    protected Component(ComponentType componentType) {
        this.componentType = componentType;
    }
}
