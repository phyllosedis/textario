package ru.phyllosedis.textario.production.assembler;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.AbstractProgressBalance;
import ru.phyllosedis.textario.resource.Tier;

@Component
public class AssemblerBalance extends AbstractProgressBalance<AssemblerBalance.AssemblerStats> {

    protected AssemblerBalance() {
        super(AssemblerStats.class);
    }

    @Override
    public AssemblerStats stats(Tier tier) {
        return switch (tier) {
            case ONE -> AssemblerStats
                    .builder()
                    .speed(1.5)
                    .build();
            case TWO -> AssemblerStats
                    .builder()
                    .speed(2.5)
                    .build();
            case THREE -> AssemblerStats
                    .builder()
                    .speed(4.0)
                    .build();
            default -> throw new IllegalArgumentException(
                    "Unknown assembler tier: " + tier
            );
        };
    }

    @Getter
    @SuperBuilder
    public static class AssemblerStats extends AbstractProgressBalance.AbstractProgressStats {
    }
}
