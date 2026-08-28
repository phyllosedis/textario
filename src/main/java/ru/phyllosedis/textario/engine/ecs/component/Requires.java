package ru.phyllosedis.textario.engine.ecs.component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Requires {
    Class<? extends Component>[] value();
}
