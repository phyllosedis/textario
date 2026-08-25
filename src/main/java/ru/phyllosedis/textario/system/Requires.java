package ru.phyllosedis.textario.system;

import ru.phyllosedis.textario.component.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Requires {
    Class<? extends Component>[] value();
}
