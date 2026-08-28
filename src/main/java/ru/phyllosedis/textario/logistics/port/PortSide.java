package ru.phyllosedis.textario.logistics.port;

import ru.phyllosedis.textario.resource.Type;

public enum PortSide implements Type<PortSide> {
    LEFT,
    RIGHT,
    FRONT,
    BACK,
    UNDEFINED;

    @Override
    public PortSide getUndefined() {
        return UNDEFINED;
    }
}
