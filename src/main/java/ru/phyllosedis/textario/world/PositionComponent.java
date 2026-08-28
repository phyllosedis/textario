package ru.phyllosedis.textario.world;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.POSITION)
public class PositionComponent extends Component {
    private final int x;
    private final int y;

    PositionComponent(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public record Args(int x, int y) implements ComponentArgs<PositionComponent> {
        @Override
        public PositionComponent instantiate() {
            return new PositionComponent(x, y);
        }
    }

}
