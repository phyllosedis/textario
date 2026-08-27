package ru.phyllosedis.textario.balance;


import ru.phyllosedis.textario.type.Tier;

// TODO можно грузить из файла
public final class InserterBalance {
    private InserterBalance() {
    }

    public static InserterStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> new InserterStats(
                    1.0, // transfer speed
                    1,   // range
                    1    // stack size
            );

            case TWO -> new InserterStats(
                    2.0,
                    1,
                    1
            );

            case THREE -> new InserterStats(
                    4.0,
                    1,
                    2
            );

            default -> throw new IllegalArgumentException(
                    "Unknown inserter tier: " + tier
            );
        };
    }

    public record InserterStats(
            double transferSpeed,
            int range,
            int stackSize
    ) {
    }
}
