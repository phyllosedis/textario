package ru.phyllosedis.textario.system.factory.annotation;

import ru.phyllosedis.textario.component.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Настройка генерации системы
 * order() - int, порядок очередности обработки в @Order от спринга
 * boost() - double, ускорение производства
 * tierMarker() - маркер тира производства
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TierConfig {
    int order();

    double boost();

    Class<? extends Component> tierMarker();
}
