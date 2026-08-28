package ru.phyllosedis.textario.engine.balance;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AbstractConveyorStats extends AbstractProgressBalance.AbstractProgressStats {
    protected double throughput;
}
