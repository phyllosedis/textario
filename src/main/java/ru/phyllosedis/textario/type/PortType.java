package ru.phyllosedis.textario.type;

public enum PortType implements Type<PortType> {
    INPUT,
    OUTPUT,
    UNDEFINED;

    @Override
    public PortType getUndefined() {
        return UNDEFINED;
    }
}
