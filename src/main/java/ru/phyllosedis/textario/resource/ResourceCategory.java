package ru.phyllosedis.textario.resource;

import lombok.Getter;
import ru.phyllosedis.textario.resource.capability.BuildSurface;
import ru.phyllosedis.textario.resource.capability.Mineable;
import ru.phyllosedis.textario.resource.capability.Pumpable;
import ru.phyllosedis.textario.resource.capability.Smeltable;

import java.util.Set;

/**
 * Задаёт начальные Capability для категории ресурса
 */
@Getter
public enum ResourceCategory implements Type<ResourceCategory> {
    ORE(
            Mineable.class,
            Smeltable.class,
            BuildSurface.class
    ),
    FLUID(
            Pumpable.class
    ),
    SOIL(
            BuildSurface.class
    ),

    UNDEFINED()
    ;
    private final Set<Class<? extends ResourceCapability>> capabilities;

    @SafeVarargs
    ResourceCategory(Class<? extends ResourceCapability>... capabilities) {
        this.capabilities = Set.of(capabilities);
    }

    @Override
    public ResourceCategory getUndefined() {
        return UNDEFINED;
    }
}
