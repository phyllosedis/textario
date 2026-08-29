package ru.phyllosedis.textario.production;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@ToString
@Getter
@AutoFactory(ComponentType.PRODUCE)
public class ProduceSpeedComponent extends Component {
    private final double speed;

    protected ProduceSpeedComponent(double speed) {
        this.speed = speed;
    }

    public record Args(double speed) implements ComponentArgs<ProduceSpeedComponent> {
        @Override
        public ProduceSpeedComponent instantiate() {
            return new ProduceSpeedComponent(speed);
        }
    }
}
