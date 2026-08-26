package ru.phyllosedis.textario.type;

public enum ComponentType implements Type<ComponentType> {
    //System
    MINING,
    INVENTORY,
    POSITION,

    // meta
    BUILDING,
    STATION,
    CONTENT_STATE,
    OPERATION_FINISHED,
    DISPATCHED_PRODUCT,
    TIER_ONE,
    TIER_TWO,
    TIER_THREE,
    TIER_FOUR,
    TIER_FIVE,

    //state
    GAS_STATE,
    LIQUID_STATE,
    SOLID_STATE,

    UNDEFINED;

    @Override
    public ComponentType getUndefined() {
        return UNDEFINED;
    }
}
