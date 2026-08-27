package ru.phyllosedis.textario.balance.transport.conveyor.splitter;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.progress.AbstractProgressBalance;
import ru.phyllosedis.textario.balance.transport.conveyor.AbstractConveyorStats;
import ru.phyllosedis.textario.type.Tier;

@Component
public class SplitterBalance extends AbstractProgressBalance<SplitterBalance.SplitterStats> {

    protected SplitterBalance() {
        super(SplitterStats.class);
    }

    public SplitterStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> SplitterStats.builder()
                    .speed(1.0)
                    .throughput(15.0)
                    .build();

            case TWO -> SplitterStats.builder()
                    .speed(2.5)
                    .throughput(30.0)
                    .build();
            case THREE -> SplitterStats.builder()
                    .speed(4.0)
                    .throughput(45.0)
                    .build();
            default -> throw new IllegalArgumentException(
                    "Unknown splitter tier: " + tier
            );
        };
    }

    @Getter
    @SuperBuilder
    public static class SplitterStats extends AbstractConveyorStats {
    }

}
