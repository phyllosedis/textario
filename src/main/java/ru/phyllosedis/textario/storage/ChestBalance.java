package ru.phyllosedis.textario.storage;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.AbstractBalance;
import ru.phyllosedis.textario.resource.Tier;

@Component
public class ChestBalance extends AbstractBalance<ChestBalance.ChestStats> {

    public ChestBalance() {
        super(ChestStats.class);
    }

    @Override
    public ChestStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> ChestStats.builder()
                    .size(8)
                    .stackSize(100)
                    .build();
            case TWO -> ChestStats.builder()
                    .size(16)
                    .stackSize(100)
                    .build();
            case THREE -> ChestStats.builder()
                    .size(32)
                    .stackSize(200)
                    .build();
            default -> throw new IllegalArgumentException(
                    "Unknown chest tier: " + tier
            );
        };
    }

    @Getter
    @SuperBuilder
    public static class ChestStats extends AbstractBalance.AbstractStats {
        private final int size;
        private final int stackSize;
    }
}
