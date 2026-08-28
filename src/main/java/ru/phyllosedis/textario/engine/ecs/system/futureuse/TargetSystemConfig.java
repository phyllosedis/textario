package ru.phyllosedis.textario.engine.ecs.system.futureuse;

import ru.phyllosedis.textario.system.AbstractSystem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Настройка конфигурации создаваемых бинов
 * targetSystemClass - абстрактный класс, от которого будут созданы бины потомки классы реализации
 * tiers - на текущий момент класс, описывающий конфигурацию для системы тиров(качества)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetSystemConfig {
    // К какому классу логики выдачи привязываем баланс (например, SolidMiningResourceSystem.class)
    Class<? extends AbstractSystem> targetSystemClass();

    // Массив тиров конкретно для этого здания
    TierConfig[] tiers();
}
