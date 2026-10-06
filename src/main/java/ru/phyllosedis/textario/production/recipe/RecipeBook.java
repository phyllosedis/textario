package ru.phyllosedis.textario.production.recipe;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Type;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Все рецепты игры с указанием станции.
 */
@Component
public class RecipeBook {

    public enum Station implements Type<Station> {
        FURNACE,
        ASSEMBLER,
        UNDEFINED;

        @Override
        public Station getUndefined() {
            return UNDEFINED;
        }
    }

    public record Recipe(String id, String name, Station station,
                         Map<ResourceType, Integer> inputs, Map<ResourceType, Integer> outputs) {
    }

    private final List<Recipe> recipes = List.of(
            new Recipe("iron-plate", "железная плита", Station.FURNACE,
                    Map.of(ResourceType.IRON_ORE, 1), Map.of(ResourceType.IRON_PLATE, 1)),
            new Recipe("copper-plate", "медная плита", Station.FURNACE,
                    Map.of(ResourceType.COPPER_ORE, 1), Map.of(ResourceType.COPPER_PLATE, 1)),
            new Recipe("iron-gear", "шестерёнки", Station.ASSEMBLER,
                    Map.of(ResourceType.IRON_PLATE, 2), Map.of(ResourceType.IRON_GEAR, 1)),
            new Recipe("copper-cable", "медный кабель", Station.ASSEMBLER,
                    Map.of(ResourceType.COPPER_PLATE, 1), Map.of(ResourceType.COPPER_CABLE, 2))
    );

    public List<Recipe> all() {
        return recipes;
    }

    public List<Recipe> byStation(Station station) {
        return recipes.stream().filter(r -> r.station() == station).toList();
    }

    public Optional<Recipe> byId(String id) {
        return recipes.stream().filter(r -> r.id().equalsIgnoreCase(id)).findFirst();
    }

    public Optional<Recipe> byIdOrName(String word) {
        String query = word.trim();
        Optional<Recipe> exact = recipes.stream()
                .filter(r -> r.id().equalsIgnoreCase(query) || r.name().equalsIgnoreCase(query))
                .findFirst();
        if (exact.isPresent()) {
            return exact;
        }
        return recipes.stream().filter(r -> r.name().toLowerCase().contains(query.toLowerCase())).findFirst();
    }

    public Optional<Recipe> furnaceRecipeFor(ResourceType input) {
        return recipes.stream()
                .filter(r -> r.station() == Station.FURNACE)
                .filter(r -> r.inputs().containsKey(input))
                .findFirst();
    }
}
