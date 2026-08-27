package ru.phyllosedis.textario.balance;

import ru.phyllosedis.textario.type.Tier;

public final class BeltBalance {
    private BeltBalance() {
    }

    public static BeltStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> new BeltStats(
                    1.0,
                    15.0
            );
            case TWO -> new BeltStats(
                    2.5,
                    30.0
            );

            case THREE -> new BeltStats(
                    4.0,
                    45.0
            );

            default -> throw new IllegalArgumentException(
                    "Unknown belt tier: " + tier
            );
        };
    }

    public record BeltStats(
            double speed,
            double throughput
    ) {
    }
}