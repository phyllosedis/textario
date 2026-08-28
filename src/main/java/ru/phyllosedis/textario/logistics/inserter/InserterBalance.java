package ru.phyllosedis.textario.logistics.inserter;


import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.AbstractBalance;
import ru.phyllosedis.textario.resource.Tier;

// TODO можно грузить из файла
@Component
public class InserterBalance extends AbstractBalance<InserterBalance.InserterStats> {

    public InserterBalance() {
        super(InserterStats.class);
    }

    public InserterStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> InserterStats.builder()
                    .transferSpeed(1.0)
                    .range(1)
                    .stackSize(1)
                    .build();
            case TWO -> InserterStats.builder()
                    .transferSpeed(2.0)
                    .range(1)
                    .stackSize(1)
                    .build();
            case THREE -> InserterStats.builder()
                    .transferSpeed(4.0)
                    .range(1)
                    .stackSize(2)
                    .build();
            default -> throw new IllegalArgumentException(
                    "Unknown inserter tier: " + tier
            );
        };
    }

    @Getter
    @SuperBuilder
    public static class InserterStats extends AbstractStats {
        private final double transferSpeed;
        private final int range;
        private final int stackSize;
    }
}
