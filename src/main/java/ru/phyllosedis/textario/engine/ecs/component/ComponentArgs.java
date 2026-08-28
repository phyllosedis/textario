package ru.phyllosedis.textario.engine.ecs.component;


public interface ComponentArgs<C extends Component> {
    C instantiate();
}
