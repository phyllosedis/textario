package ru.phyllosedis.textario.engine.ecs.system.futureuse;

import ru.phyllosedis.textario.engine.ecs.component.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Настройка генерации бина, в данном случае тира
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
