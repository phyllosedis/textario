package ru.phyllosedis.textario.system;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.Ordered;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;

import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractSystem implements GameSystem, Ordered {
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
