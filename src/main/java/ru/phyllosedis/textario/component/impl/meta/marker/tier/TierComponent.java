package ru.phyllosedis.textario.component.impl.meta.marker.tier;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.Tier;

@Getter
@ToString
@AutoFactory(ComponentType.TIER)
public class TierComponent extends Component implements BuildingTier {
    private final int tier;

    protected TierComponent(int tier) {
        super();
        this.tier = tier;
    }

    public record Args(Tier tier) implements ComponentArgs<TierComponent> {
        @Override
        public TierComponent instantiate() {
            return new TierComponent(tier.ordinal());
        }
    }


}
