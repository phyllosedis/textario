package ru.phyllosedis.textario.component.impl.meta.tier;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.factory.ComponentFactory;
import ru.phyllosedis.textario.type.ComponentType;

@Component
public class TierOneMarkerComponentFactory extends ComponentFactory<TierOneMarkerComponent, TierOneMarkerComponent.Args> {

    public TierOneMarkerComponentFactory() {
        super(ComponentType.TIER_ONE, TierOneMarkerComponent.Args.class);
    }

    @Override
    public TierOneMarkerComponent create(TierOneMarkerComponent.Args componentArgs) {
        return new TierOneMarkerComponent();
    }
}
