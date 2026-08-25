package ru.phyllosedis.textario.type;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ResourceType {
    IRON_ORE,
    COPPER_ORE,
    UNDEFINED;

    private static final Map<Integer, ResourceType> BY_ORDINAL =
            Arrays.stream(ResourceType.values())
                    .collect(Collectors.toMap(Enum::ordinal, e -> e));

    public static ResourceType getByOrdinal(int ordinal) {
        ResourceType resourceType = BY_ORDINAL.get(ordinal);
        if (resourceType == null) {
            return ResourceType.UNDEFINED;
        }
        return resourceType;
    }

}
