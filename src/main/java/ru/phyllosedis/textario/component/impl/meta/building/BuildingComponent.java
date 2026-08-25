package ru.phyllosedis.textario.component.impl.meta.building;

import lombok.Getter;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
public class BuildingComponent extends Component {

    private final int width;
    private final int height;

    protected BuildingComponent(int width, int height) {
        super(ComponentType.BUILDING);
        this.width = width;
        this.height = height;
    }

    public record Args(int width, int height) implements ComponentArgs<BuildingComponent> {
    }
}
