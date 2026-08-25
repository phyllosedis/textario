package ru.phyllosedis.textario.component.factory;

import lombok.Getter;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
public abstract class ComponentFactory<
        Component extends ru.phyllosedis.textario.component.Component,
        ComponentArgs extends ru.phyllosedis.textario.component.factory.ComponentArgs<Component>> {

    public abstract Class<ComponentArgs> getArgsClass();

    private final ComponentType componentType;

    public ComponentFactory(ComponentType componentType) {
        this.componentType = componentType;
    }

    public abstract Component create(ComponentArgs componentArgs);

}

