package ru.phyllosedis.textario.balance;

import ru.phyllosedis.textario.type.Tier;

public final class SplitterBalance {
    private SplitterBalance() {
    }

    public static SplitterStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> new SplitterStats(1.0, 15.0);
            case TWO -> new SplitterStats(2.5, 30.0);
            case THREE -> new SplitterStats(4.0, 45.0);
            default -> throw new IllegalArgumentException(
                    "Unknown splitter tier: " + tier
            );
        };
    }

    public record SplitterStats(
            double speed,
            double throughput
    ) {
    }
}
