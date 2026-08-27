package ru.phyllosedis.textario.component.impl.meta.marker.tier;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.TIER)
public class TierComponent extends Component implements BuildingTier {
    protected TierComponent() {
        super();
    }

    public record Args() implements ComponentArgs<TierComponent> {
        @Override
        public TierComponent instantiate() {
            return new TierComponent();
        }
    }


}
