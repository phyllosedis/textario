package ru.phyllosedis.textario.type;

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
