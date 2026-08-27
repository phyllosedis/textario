package ru.phyllosedis.textario.component.impl.mining;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

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
