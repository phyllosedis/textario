package ru.phyllosedis.textario.resource.marker;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.MARKER)
public class GasStateMarkerComponent extends Component {
    protected GasStateMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<GasStateMarkerComponent> {
        @Override
        public GasStateMarkerComponent instantiate() {
            return new GasStateMarkerComponent();
        }
    }
}
