package ru.phyllosedis.textario.service.factory.marker;

import ru.phyllosedis.textario.component.impl.meta.marker.MarkerComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssociatedMarker {
    Class<? extends MarkerComponent> value();
}
