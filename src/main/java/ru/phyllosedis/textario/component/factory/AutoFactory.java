package ru.phyllosedis.textario.component.factory;

import ru.phyllosedis.textario.type.ComponentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFactory {
    ComponentType value();
}
