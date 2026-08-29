package ru.phyllosedis.textario.resource;

import lombok.Getter;
import ru.phyllosedis.textario.resource.capability.*;

import java.util.Arrays;
import java.util.Set;

/**
 * Задаёт начальные Capability для категории ресурса
 */
@Getter
public enum ResourceCategory implements Type<ResourceCategory> {
    ORE(
            Mineable.class,
            Smeltable.class,
            BuildSurface.class,
            SolidTransportable.class
    ),
    FLUID(
            Pumpable.class,
            LiquidTransportable.class
    ),
    SOIL(
            BuildSurface.class
    ),

    UNDEFINED();
    private final Set<Class<? extends ResourceCapability>> capabilities;

    @SafeVarargs
    ResourceCategory(Class<? extends ResourceCapability>... capabilities) {
        this.capabilities = Set.of(capabilities);
    }

    public boolean hasCapability(
            Class<? extends ResourceCapability> capability
    ) {
        return capabilities.contains(capability);
    }

    @SafeVarargs
    public final boolean hasAllCapabilities(
            Class<? extends ResourceCapability>... required
    ) {
        return Arrays.stream(required)
                .allMatch(capabilities::contains);
    }

    @Override
    public ResourceCategory getUndefined() {
        return UNDEFINED;
    }
}
