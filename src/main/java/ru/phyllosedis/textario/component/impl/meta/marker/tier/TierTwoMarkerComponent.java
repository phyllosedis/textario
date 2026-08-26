package ru.phyllosedis.textario.component.impl.meta.marker.tier;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.TIER_TWO)
public class TierTwoMarkerComponent extends AbstractTierComponent {
    protected TierTwoMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<TierTwoMarkerComponent> {
        @Override
        public TierTwoMarkerComponent instantiate() {
            return new TierTwoMarkerComponent();
        }
    }
}
