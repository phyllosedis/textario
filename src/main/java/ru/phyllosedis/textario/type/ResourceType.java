package ru.phyllosedis.textario.type;

public enum ResourceType implements Type<ResourceType> {
    IRON_ORE,
    COPPER_ORE,
    NONE,
    UNDEFINED;

    @Override
    public ResourceType getUndefined() {
        return UNDEFINED;
    }

}
