package ru.phyllosedis.textario.logistics.belt;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.AbstractProgressBalance;
import ru.phyllosedis.textario.engine.balance.AbstractConveyorStats;
import ru.phyllosedis.textario.resource.Tier;

@Component
public class BeltBalance extends AbstractProgressBalance<BeltBalance.BeltStats> {

    public BeltBalance() {
        super(BeltStats.class);
    }

    @Override
    public BeltStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> BeltStats.builder()
                    .speed(1.0)
                    .throughput(15.0)
                    .build();
            case TWO -> BeltStats.builder()
                    .speed(2.5)
                    .throughput(30.0)
                    .build();
            case THREE -> BeltStats.builder()
                    .speed(4.0)
                    .throughput(45.0)
                    .build();
            default -> throw new IllegalArgumentException(
                    "Unknown belt tier: " + tier
            );
        };
    }

    @Getter
    @SuperBuilder
    public static class BeltStats extends AbstractConveyorStats {
    }
}