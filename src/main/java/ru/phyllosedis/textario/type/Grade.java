package ru.phyllosedis.textario.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum Grade {
    TIER_1(1),
    TIER_2(1.15f),
    TIER_3(1.25f),
    TIER_4(1.5f),
    TIER_5(1.75f);

    private final float boost;

    Grade(float boost) {
        this.boost = boost;
    }

    private static final Map<Integer, Grade> BY_ORDINAL =
            Arrays.stream(Grade.values())
                    .collect(Collectors.toMap(Enum::ordinal, e -> e));

    public static Grade getByOrdinal(int ordinal) {
        Grade grade = BY_ORDINAL.get(ordinal);
        return grade != null ? grade : TIER_1; // По умолчанию 1-й тир
    }
}
