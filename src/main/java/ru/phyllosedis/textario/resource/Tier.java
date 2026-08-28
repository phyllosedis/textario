package ru.phyllosedis.textario.resource;

public enum Tier implements Type<Tier> {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    UNDEFINED;

    @Override
    public Tier getUndefined() {
        return UNDEFINED;
    }
}
