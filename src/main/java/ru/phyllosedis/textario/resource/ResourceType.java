package ru.phyllosedis.textario.resource;

import lombok.Getter;
import ru.phyllosedis.textario.resource.capability.Burnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Содержит все типы ресурсов, их физическое состояние (газ, жидкость, твёрдое), типы взаимодействий, которые возможны с ресурсом
 *
 */
@Getter
public enum ResourceType implements Type<ResourceType> {
    IRON_ORE(
            ContentState.SOLID,
            ResourceCategory.ORE
    ),

    COPPER_ORE(
            ContentState.SOLID,
            ResourceCategory.ORE
    ),

    COAL(
            ContentState.SOLID,
            ResourceCategory.ORE,
            Set.of(Burnable.class),
            Set.of()
    ),

    EARTH(
            ContentState.SOLID,
            ResourceCategory.SOIL
    ),
    WATER(
            ContentState.LIQUID,
            ResourceCategory.FLUID
    ),
    UNDEFINED(ContentState.UNDEFINED,
            null
    );

    private final ContentState state;
    private final ResourceCategory category;
    private final Set<Class<? extends ResourceCapability>> capabilities;

    ResourceType(
            ContentState state,
            ResourceCategory category
    ) {
        this(state, category, Set.of(), Set.of());
    }

    ResourceType(
            ContentState state,
            ResourceCategory category,
            Set<Class<? extends ResourceCapability>> additionalCapabilities,
            Set<Class<? extends ResourceCapability>> excludedCapabilities
    ) {
        this.state = state;
        this.category = category;

        var result = Set.<Class<? extends ResourceCapability>>copyOf(
                category == null
                        ? Set.of()
                        : category.getCapabilities()
        );

        var mutable = new java.util.HashSet<>(result);

        mutable.addAll(additionalCapabilities);
        mutable.removeAll(excludedCapabilities);

        this.capabilities = Collections.unmodifiableSet(mutable);
    }


    public boolean hasExactCategory(
            ResourceCategory requestCategory
    ) {
        return this.category.equals(requestCategory);
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
    public ResourceType getUndefined() {
        return UNDEFINED;
    }

}
