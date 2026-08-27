package ru.phyllosedis.textario.component.impl.meta.marker.station;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.component.impl.meta.marker.MarkerComponent;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.STATION)
public class StationComponent extends MarkerComponent {

    protected StationComponent() {
        super();
    }

    public record Args() implements ComponentArgs<StationComponent> {
        @Override
        public StationComponent instantiate() {
            return new StationComponent();
        }
    }


}
