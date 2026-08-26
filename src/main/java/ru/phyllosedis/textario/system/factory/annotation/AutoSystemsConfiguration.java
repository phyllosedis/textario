package ru.phyllosedis.textario.system.factory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Настройка на количество и тип генерируемых систем
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoSystemsConfiguration {
    TargetSystemConfig[] value();
}
