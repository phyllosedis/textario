package ru.phyllosedis.textario.component.factory;

import lombok.Getter;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
public abstract class ComponentFactory<
        Component extends ru.phyllosedis.textario.component.Component,
        ComponentArgs extends ru.phyllosedis.textario.component.factory.ComponentArgs<Component>> {

    private final ComponentType componentType;
    private final Class<ComponentArgs> argsClass;

    public ComponentFactory(ComponentType componentType, Class<ComponentArgs> argsClass) {
        this.componentType = componentType;
        this.argsClass = argsClass;
    }

    public abstract Component create(ComponentArgs componentArgs);

}

