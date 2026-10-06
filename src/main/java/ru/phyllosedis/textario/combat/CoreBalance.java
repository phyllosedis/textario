package ru.phyllosedis.textario.combat;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.AbstractBalance;
import ru.phyllosedis.textario.resource.Tier;

@Component
public class CoreBalance extends AbstractBalance<CoreBalance.CoreStats> {

    public CoreBalance() {
        super(CoreStats.class);
    }

    @Override
    public CoreStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> CoreStats.builder().hp(1000).build();
            case TWO -> CoreStats.builder().hp(2000).build();
            case THREE -> CoreStats.builder().hp(4000).build();
            default -> throw new IllegalArgumentException("Unknown core tier: " + tier);
        };
    }

    @Getter
    @SuperBuilder
    public static class CoreStats extends AbstractBalance.AbstractStats {
        private final int hp;
    }
}
