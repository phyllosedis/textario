package ru.phyllosedis.textario.component.impl.meta.building;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.factory.ComponentFactory;
import ru.phyllosedis.textario.type.ComponentType;

@Component
public class BuildingComponentFactory extends ComponentFactory<BuildingComponent, BuildingComponent.Args> {

    public BuildingComponentFactory() {
        super(ComponentType.BUILDING, BuildingComponent.Args.class);
    }

    @Override
    public BuildingComponent create(BuildingComponent.Args componentArgs) {
        return new BuildingComponent(componentArgs.width(), componentArgs.height());
    }
}
