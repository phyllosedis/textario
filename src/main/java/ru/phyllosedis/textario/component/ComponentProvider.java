package ru.phyllosedis.textario.component;

import ru.phyllosedis.textario.component.factory.ComponentArgs;

import java.util.function.Function;

public interface ComponentProvider<ComponentType, ComponentArgsType extends ComponentArgs> extends Function<ComponentArgsType, ComponentType> {
    Class<ComponentType> getComponentClass();
}
