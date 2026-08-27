package ru.phyllosedis.textario.balance.miner;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.progress.AbstractProgressBalance;
import ru.phyllosedis.textario.type.Tier;

@Component
public class MinerBalance extends AbstractProgressBalance<MinerBalance.MinerStats> {

    protected MinerBalance() {
        super(MinerStats.class);
    }

    @Override
    public MinerStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> MinerStats
                    .builder()
                    .speed(1.0)
                    .build();
            case TWO -> MinerStats
                    .builder()
                    .speed(1.15)
                    .build();
            case THREE -> MinerStats
                    .builder()
                    .speed(1.5)
                    .build();
            default -> throw new IllegalArgumentException(
                    "Unknown miner tier: " + tier
            );
        };
    }

    @Getter
    @SuperBuilder
    public static class MinerStats extends AbstractProgressBalance.AbstractProgressStats {
    }
}
