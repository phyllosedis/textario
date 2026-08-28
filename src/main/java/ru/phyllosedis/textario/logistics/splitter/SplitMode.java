package ru.phyllosedis.textario.logistics.splitter;

import ru.phyllosedis.textario.resource.Type;

public enum SplitMode implements Type<SplitMode> {
    ROUND_ROBIN,
    BALANCED,
    PRIORITY_LEFT,
    PRIORITY_RIGHT,
    UNDEFINED;

    @Override
    public SplitMode getUndefined() {
        return UNDEFINED;
    }
}
