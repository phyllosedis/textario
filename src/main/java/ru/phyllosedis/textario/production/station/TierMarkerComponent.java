package ru.phyllosedis.textario.production.station;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.MarkerComponent;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.TIER)
public class TierMarkerComponent extends Component implements MarkerComponent {
    protected TierMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<TierMarkerComponent> {
        @Override
        public TierMarkerComponent instantiate() {
            return new TierMarkerComponent();
        }
    }


}
