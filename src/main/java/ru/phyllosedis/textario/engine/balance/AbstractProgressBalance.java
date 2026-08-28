package ru.phyllosedis.textario.engine.balance;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

public abstract class AbstractProgressBalance<C extends AbstractProgressBalance.AbstractProgressStats> extends AbstractBalance<C> {

    protected AbstractProgressBalance(Class<C> stats) {
        super(stats);
    }

    @Getter
    @SuperBuilder
    public static class AbstractProgressStats extends AbstractStats {
        protected double speed;
    }
}
