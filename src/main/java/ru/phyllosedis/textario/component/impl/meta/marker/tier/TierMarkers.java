package ru.phyllosedis.textario.component.impl.meta.marker.tier;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class TierMarkers {
    private static final Map<Integer, Supplier<AbstractTierComponent>> MARKERS = Map.of(
            1, TierOneMarkerComponent::new,
            2, TierTwoMarkerComponent::new
    );

    public static AbstractTierComponent get(int tier) {
        return Optional.ofNullable(MARKERS.get(tier))
                .orElseThrow(() -> new IllegalArgumentException("Неподдерживаемый тир постройки: " + tier))
                .get();
    }
}
