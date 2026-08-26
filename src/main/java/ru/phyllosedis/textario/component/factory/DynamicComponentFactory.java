package ru.phyllosedis.textario.component.factory;

import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.type.ComponentType;

public class DynamicComponentFactory<C extends Component, A extends ComponentArgs<C>> extends ComponentFactory<C, A> {
    public DynamicComponentFactory(ComponentType componentType, Class<A> aClass) {
        super(componentType, aClass);
    }

    @Override
    public C create(A componentArgs) {
        return componentArgs.instantiate();
    }
}
