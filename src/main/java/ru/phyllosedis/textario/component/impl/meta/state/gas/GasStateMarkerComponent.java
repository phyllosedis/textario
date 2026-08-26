package ru.phyllosedis.textario.component.impl.meta.state.gas;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.GAS_STATE)
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
