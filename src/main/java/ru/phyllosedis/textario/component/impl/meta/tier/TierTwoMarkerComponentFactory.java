package ru.phyllosedis.textario.component.impl.meta.tier;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.factory.ComponentFactory;
import ru.phyllosedis.textario.type.ComponentType;

@Component
public class TierTwoMarkerComponentFactory extends ComponentFactory<TierTwoMarkerComponent, TierTwoMarkerComponent.Args> {
    public TierTwoMarkerComponentFactory() {
        super(ComponentType.TIER_TWO);
    }

    @Override
    public Class<TierTwoMarkerComponent.Args> getArgsClass() {
        return TierTwoMarkerComponent.Args.class;
    }

    @Override
    public TierTwoMarkerComponent create(TierTwoMarkerComponent.Args componentArgs) {
        return new TierTwoMarkerComponent();
    }
}
