package ru.phyllosedis.textario.resource;

public enum ContentState implements Type<ContentState> {
    SOLID,
    LIQUID,
    GAS,
    UNDEFINED;

    @Override
    public ContentState getUndefined() {
        return UNDEFINED;
    }

}
