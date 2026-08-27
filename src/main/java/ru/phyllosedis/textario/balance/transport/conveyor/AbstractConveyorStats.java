package ru.phyllosedis.textario.balance.transport.conveyor;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.phyllosedis.textario.balance.progress.AbstractProgressBalance;

@Getter
@SuperBuilder
public class AbstractConveyorStats extends AbstractProgressBalance.AbstractProgressStats {
    protected double throughput;
}
