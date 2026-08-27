package ru.phyllosedis.textario.balance;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.phyllosedis.textario.type.Tier;

public abstract class AbstractBalance<C extends AbstractBalance.AbstractStats> {
    @Getter
    private Class<C> clazz;

    protected AbstractBalance(Class<C> clazz) {
        this.clazz = clazz;
    }

    public abstract C stats(Tier tier);

    @SuperBuilder
    public abstract static class AbstractStats {
    }
}
