package ru.phyllosedis.textario.system;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.Ordered;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.engine.ecs.system.System;

import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractSystem implements System, Ordered {
    @Getter
    @Setter
    private int order = 0;
    protected final ComponentFactoryManager cfm;
    protected final ComponentManager cm;

    @Getter
    @Setter
    private Set<Class<? extends Component>> requiredComponents;

    @Override
    public void update() {
        Set<Long> targets = cm.getEntitiesForSystem(this);
        for (long id : targets) {
            updateEntity(id);
        }
    }

    protected abstract void updateEntity(long id);
}
