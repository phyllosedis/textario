package ru.phyllosedis.textario.combat;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.AbstractBalance;
import ru.phyllosedis.textario.resource.Tier;

@Component
public class TurretBalance extends AbstractBalance<TurretBalance.TurretStats> {

    public TurretBalance() {
        super(TurretStats.class);
    }

    @Override
    public TurretStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> TurretStats.builder()
                    .range(6).damage(3).interval(40).hp(150).build();
            case TWO -> TurretStats.builder()
                    .range(8).damage(5).interval(30).hp(250).build();
            case THREE -> TurretStats.builder()
                    .range(10).damage(8).interval(20).hp(400).build();
            default -> throw new IllegalArgumentException("Unknown turret tier: " + tier);
        };
    }

    @Getter
    @SuperBuilder
    public static class TurretStats extends AbstractBalance.AbstractStats {
        private final int range;
        private final int damage;
        private final int interval;
        private final int hp;
    }
}
