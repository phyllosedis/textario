package ru.phyllosedis.textario.component.impl.meta.tier;

import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

public class TierTwoMarkerComponent extends Component implements BuildingTier {
    protected TierTwoMarkerComponent() {
        super(ComponentType.TIER_TWO);
    }

    public record Args() implements ComponentArgs<TierTwoMarkerComponent> {
    }
}
