package ru.phyllosedis.textario.component.impl.meta.tier;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.TIER_ONE)
public class TierOneMarkerComponent extends Component implements BuildingTier {
    protected TierOneMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<TierOneMarkerComponent> {
        @Override
        public TierOneMarkerComponent instantiate() {
            return new TierOneMarkerComponent();
        }
    }
}
