package ru.phyllosedis.textario.system.factory.annotation;

import ru.phyllosedis.textario.system.AbstractSystem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TargetSystemConfig {
    // К какому классу логики выдачи привязываем баланс (например, SolidMiningResourceSystem.class)
    Class<? extends AbstractSystem> targetSystemClass();

    // Массив тиров конкретно для этого здания
    TierConfig[] tiers();
}
