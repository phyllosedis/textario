package ru.phyllosedis.textario.production.station;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.MarkerComponent;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.STATION)
public class StationMarkerComponent extends Component implements MarkerComponent {

    protected StationMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<StationMarkerComponent> {
        @Override
        public StationMarkerComponent instantiate() {
            return new StationMarkerComponent();
        }
    }


}
