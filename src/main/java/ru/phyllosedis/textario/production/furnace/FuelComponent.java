package ru.phyllosedis.textario.production.furnace;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

/**
 * Запас жара в печи: сколько плавок осталось от загруженного топлива.
 * 1 единица угля = 4 плавки.
 */
@Getter
@ToString
@AutoFactory(ComponentType.FUEL)
public class FuelComponent extends Component {

    private final int heat;

    protected FuelComponent(int heat) {
        super();
        this.heat = Math.max(0, heat);
    }

    public record Args(int heat) implements ComponentArgs<FuelComponent> {
        @Override
        public FuelComponent instantiate() {
            return new FuelComponent(heat);
        }
    }
}
