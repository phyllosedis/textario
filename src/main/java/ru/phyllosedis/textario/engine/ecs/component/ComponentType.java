package ru.phyllosedis.textario.engine.ecs.component;

import ru.phyllosedis.textario.resource.Type;

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

    LOGISTIC,
    UNDEFINED;

    @Override
    public ComponentType getUndefined() {
        return UNDEFINED;
    }
}
