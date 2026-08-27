package ru.phyllosedis.textario.component.impl.meta.marker.state.liquid;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.LIQUID_STATE)
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
