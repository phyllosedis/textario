package ru.phyllosedis.textario.component.impl.position;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.factory.ComponentFactory;
import ru.phyllosedis.textario.type.ComponentType;

@Component
public class PositionComponentFactory extends ComponentFactory<PositionComponent, PositionComponent.Args> {

    public PositionComponentFactory() {
        super(ComponentType.POSITION, PositionComponent.Args.class);
    }

    @Override
    public PositionComponent create(PositionComponent.Args componentArgs) {
        return new PositionComponent(componentArgs.x(), componentArgs.y());
    }
}
