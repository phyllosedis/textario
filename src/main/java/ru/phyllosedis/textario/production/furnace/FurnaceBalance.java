package ru.phyllosedis.textario.production.furnace;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.AbstractProgressBalance;
import ru.phyllosedis.textario.resource.Tier;

@Component
public class FurnaceBalance extends AbstractProgressBalance<FurnaceBalance.FurnaceStats> {

    protected FurnaceBalance() {
        super(FurnaceStats.class);
    }

    @Override
    public FurnaceStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> FurnaceStats
                    .builder()
                    .speed(2.0)
                    .build();
            case TWO -> FurnaceStats
                    .builder()
                    .speed(3.0)
                    .build();
            case THREE -> FurnaceStats
                    .builder()
                    .speed(5.0)
                    .build();
            default -> throw new IllegalArgumentException(
                    "Unknown furnace tier: " + tier
            );
        };
    }

    @Getter
    @SuperBuilder
    public static class FurnaceStats extends AbstractProgressBalance.AbstractProgressStats {
    }
}
