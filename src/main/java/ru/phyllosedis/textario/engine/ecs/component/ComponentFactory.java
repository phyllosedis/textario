package ru.phyllosedis.textario.engine.ecs.component;

import lombok.Getter;

@Getter
public abstract class ComponentFactory<
        Component extends ru.phyllosedis.textario.engine.ecs.component.Component,
        ComponentArgs extends ru.phyllosedis.textario.engine.ecs.component.ComponentArgs<Component>> {

    private final ComponentType componentType;
    private final Class<ComponentArgs> argsClass;

    public ComponentFactory(ComponentType componentType, Class<ComponentArgs> argsClass) {
        this.componentType = componentType;
        this.argsClass = argsClass;
    }

    public abstract Component create(ComponentArgs componentArgs);

}

