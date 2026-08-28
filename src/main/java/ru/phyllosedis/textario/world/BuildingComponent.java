package ru.phyllosedis.textario.world;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

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
