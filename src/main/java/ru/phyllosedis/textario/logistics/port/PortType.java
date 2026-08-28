package ru.phyllosedis.textario.logistics.port;

import ru.phyllosedis.textario.resource.Type;

public enum PortType implements Type<PortType> {
    INPUT,
    OUTPUT,
    UNDEFINED;

    @Override
    public PortType getUndefined() {
        return UNDEFINED;
    }
}
