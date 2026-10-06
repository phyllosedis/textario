package ru.phyllosedis.textario.logistics.underground;

import ru.phyllosedis.textario.resource.Type;

public enum UndergroundMode implements Type<UndergroundMode> {
    ENTRY,
    EXIT,
    UNDEFINED;

    @Override
    public UndergroundMode getUndefined() {
        return UNDEFINED;
    }
}
