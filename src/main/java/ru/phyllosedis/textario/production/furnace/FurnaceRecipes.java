package ru.phyllosedis.textario.production.furnace;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.Map;
import java.util.Optional;

@Component
public class FurnaceRecipes {

    private final Map<ResourceType, ResourceType> recipes = Map.of(
            ResourceType.IRON_ORE, ResourceType.IRON_PLATE,
            ResourceType.COPPER_ORE, ResourceType.COPPER_PLATE
    );

    public Optional<ResourceType> outputFor(ResourceType input) {
        return Optional.ofNullable(recipes.get(input));
    }

    public boolean isSmeltable(ResourceType input) {
        return recipes.containsKey(input);
    }
}
