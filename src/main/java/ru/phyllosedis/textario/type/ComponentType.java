package ru.phyllosedis.textario.type;

public enum ComponentType implements Type<ComponentType> {
    //System
    MINING,
    INVENTORY,
    POSITION,

    // meta
    MARKER,
    BUILDING,
    STATION,
    STATION_PROGRESS,
    EXTRACTOR,
    CONTENT_STATE,
    OPERATION_FINISHED,
    DISPATCHED_PRODUCT,
    TIER,

    //state
    GAS_STATE,
    LIQUID_STATE,
    SOLID_STATE,

    //transport
    TRANSPORT,
    UNDEFINED;

    @Override
    public ComponentType getUndefined() {
        return UNDEFINED;
    }
}
