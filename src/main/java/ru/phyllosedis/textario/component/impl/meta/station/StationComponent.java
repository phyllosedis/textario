package ru.phyllosedis.textario.component.impl.meta.station;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.STATION)
public class StationComponent extends Component {

    private final double speed;
    private final double progress;

    protected StationComponent(double speed, double progress) {
        super();
        this.speed = speed;
        this.progress = progress;
    }

    public record Args(double speed, double progress) implements ComponentArgs<StationComponent> {
        @Override
        public StationComponent instantiate() {
            return new StationComponent(speed, progress);
        }
    }


}
