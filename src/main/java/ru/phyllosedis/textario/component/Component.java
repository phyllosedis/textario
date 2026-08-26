package ru.phyllosedis.textario.component;

import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.type.ComponentType;

public abstract class Component {
    public final ComponentType componentType;

    protected Component() {
        // Автоматически вытаскиваем тип из аннотации @AutoFactory над конкретным классом
        AutoFactory autoFactoryAnn = this.getClass().getAnnotation(AutoFactory.class);
        if (autoFactoryAnn == null) {
            throw new IllegalStateException(String.format(
                    "Критическая ошибка! Компонент %s обязан быть помечен аннотацией @AutoFactory",
                    this.getClass().getSimpleName()
            ));
        }
        this.componentType = autoFactoryAnn.value();
    }
}
