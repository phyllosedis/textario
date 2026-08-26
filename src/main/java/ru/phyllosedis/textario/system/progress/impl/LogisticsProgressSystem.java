package ru.phyllosedis.textario.system.progress.impl;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierOneMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierTwoMarkerComponent;
import ru.phyllosedis.textario.system.belt.BeltSystem;
import ru.phyllosedis.textario.system.factory.annotation.AutoSystemsConfiguration;
import ru.phyllosedis.textario.system.factory.annotation.TargetSystemConfig;
import ru.phyllosedis.textario.system.factory.annotation.TierConfig;
import ru.phyllosedis.textario.system.inserter.InserterSystem;
import ru.phyllosedis.textario.system.progress.AbstractProcessSystem;


@AutoSystemsConfiguration({
        // Блоки баланса для логистики предметов
        @TargetSystemConfig(
                targetSystemClass = BeltSystem.class,
                tiers = {
                        @TierConfig(order = 15, boost = 1.0, tierMarker = TierOneMarkerComponent.class), // Желтый конвейер
                        @TierConfig(order = 15, boost = 2.0, tierMarker = TierTwoMarkerComponent.class)  // Красный конвейер
                }
        ),
        @TargetSystemConfig(
                targetSystemClass = InserterSystem.class,
                tiers = {
                        @TierConfig(order = 15, boost = 1.2, tierMarker = TierOneMarkerComponent.class), // Обычная рука
                        @TierConfig(order = 15, boost = 2.5, tierMarker = TierTwoMarkerComponent.class)  // Быстрая рука
                }
        )
})
public class LogisticsProgressSystem extends AbstractProcessSystem {
    public LogisticsProgressSystem(ComponentFactoryManager cfm, ComponentManager cm, double boost) {
        super(cfm, cm, boost);
    }
}
