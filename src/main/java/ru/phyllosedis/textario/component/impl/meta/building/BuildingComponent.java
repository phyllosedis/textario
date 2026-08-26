package ru.phyllosedis.textario.component.impl.meta.building;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.BUILDING)
public class BuildingComponent extends Component {

    private final int width;
    private final int height;

    protected BuildingComponent(int width, int height) {
        super();
        this.width = width;
        this.height = height;
    }

    public record Args(int width, int height) implements ComponentArgs<BuildingComponent> {
        @Override
        public BuildingComponent instantiate() {
            return new BuildingComponent(width, height);
        }
    }
}
