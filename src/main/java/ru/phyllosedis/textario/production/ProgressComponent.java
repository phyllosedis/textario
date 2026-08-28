package ru.phyllosedis.textario.production;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.STATION_PROGRESS)
public class ProgressComponent extends Component {
    private final double progress;

    protected ProgressComponent(double progress) {
        this.progress = progress;
    }

    public record Args(double progress) implements ComponentArgs<ProgressComponent> {
        @Override
        public ProgressComponent instantiate() {
            return new ProgressComponent(progress);
        }
    }
}
