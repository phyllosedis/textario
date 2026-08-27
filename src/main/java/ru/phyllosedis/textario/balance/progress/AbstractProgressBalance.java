package ru.phyllosedis.textario.balance.progress;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.phyllosedis.textario.balance.AbstractBalance;

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
