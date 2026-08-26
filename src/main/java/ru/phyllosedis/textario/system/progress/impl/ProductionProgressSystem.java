package ru.phyllosedis.textario.system.progress.impl;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.station.StationComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierOneMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierTwoMarkerComponent;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.system.factory.annotation.AutoSystemsConfiguration;
import ru.phyllosedis.textario.system.factory.annotation.TargetSystemConfig;
import ru.phyllosedis.textario.system.factory.annotation.TierConfig;
import ru.phyllosedis.textario.system.mining.liquid.LiquidMiningResourceSystem;
import ru.phyllosedis.textario.system.mining.solid.SolidMiningResourceSystem;
import ru.phyllosedis.textario.system.progress.AbstractProcessSystem;

@Requires({StationComponent.class})
@AutoSystemsConfiguration({
        // БЛОК №1: Твердотельная добыча (Буры руды) — 3 тира
        @TargetSystemConfig(
                targetSystemClass = SolidMiningResourceSystem.class,
                tiers = {
                        @TierConfig(order = 10, boost = 1.0, tierMarker = TierOneMarkerComponent.class),
                        @TierConfig(order = 10, boost = 1.15, tierMarker = TierTwoMarkerComponent.class),
                }
        ),
        // БЛОК №2: Жидкостная добыча (Помпы нефти) — 2 тира, свои скорости
        @TargetSystemConfig(
                targetSystemClass = LiquidMiningResourceSystem.class,
                tiers = {
                        @TierConfig(order = 10, boost = 1.0, tierMarker = TierOneMarkerComponent.class),
                        @TierConfig(order = 10, boost = 1.50, tierMarker = TierTwoMarkerComponent.class)
                }
        )
        // В будущем сюда же в один клик добавляются @TargetSystemConfig для заводов, плавилен и т.д.
})
public class ProductionProgressSystem extends AbstractProcessSystem {
    public ProductionProgressSystem(ComponentFactoryManager cfm, ComponentManager cm, double boost) {
        super(cfm, cm, boost);
    }
}
