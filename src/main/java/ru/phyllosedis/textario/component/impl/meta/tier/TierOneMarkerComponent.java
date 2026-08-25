package ru.phyllosedis.textario.component.impl.meta.tier;

import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

public class TierOneMarkerComponent extends Component {
    protected TierOneMarkerComponent() {
        super(ComponentType.TIER_ONE);
    }

    public record Args() implements ComponentArgs<TierOneMarkerComponent> {
    }
}
