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
public class LiquidStateMarkerComponent extends Component {
    protected LiquidStateMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<LiquidStateMarkerComponent> {
        @Override
        public LiquidStateMarkerComponent instantiate() {
            return new LiquidStateMarkerComponent();
        }
    }
}
