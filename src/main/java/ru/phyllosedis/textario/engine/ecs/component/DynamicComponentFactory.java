package ru.phyllosedis.textario.engine.ecs.component;

public class DynamicComponentFactory<C extends Component, A extends ComponentArgs<C>> extends ComponentFactory<C, A> {
    public DynamicComponentFactory(ComponentType componentType, Class<A> aClass) {
        super(componentType, aClass);
    }

    @Override
    public C create(A componentArgs) {
        return componentArgs.instantiate();
    }
}
