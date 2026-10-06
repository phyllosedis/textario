package ru.phyllosedis.textario.world;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

/**
 * Поворот постройки: четверти оборота по часовой стрелке (0..3).
 * 0 — как построили (FRONT смотрит вниз, +Y).
 * Порты BACK/FRONT/LEFT/RIGHT поворачиваются вместе с корпусом.
 */
@Getter
@ToString
@AutoFactory(ComponentType.ROTATION)
public class RotationComponent extends Component {

    private final int steps;

    protected RotationComponent(int steps) {
        super();
        this.steps = ((steps % 4) + 4) % 4;
    }

    public record Args(int steps) implements ComponentArgs<RotationComponent> {
        @Override
        public RotationComponent instantiate() {
            return new RotationComponent(steps);
        }
    }
}
