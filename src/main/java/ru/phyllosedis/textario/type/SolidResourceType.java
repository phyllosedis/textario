package ru.phyllosedis.textario.type;

public enum SolidResourceType implements Type<SolidResourceType>{
    UNDEFINED
    ;

    @Override
    public SolidResourceType getUndefined() {
        return UNDEFINED;
    }
}
