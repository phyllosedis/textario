package ru.phyllosedis.textario.type;

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
