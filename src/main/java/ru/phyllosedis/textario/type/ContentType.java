package ru.phyllosedis.textario.type;

public enum ContentType implements Type<ContentType> {
    SOLID,
    LIQUID,
    GAS,
    UNDEFINED;

    @Override
    public ContentType getUndefined() {
        return UNDEFINED;
    }

}
