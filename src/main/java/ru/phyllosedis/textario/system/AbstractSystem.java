package ru.phyllosedis.textario.system;

import lombok.Getter;
import lombok.Setter;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;

import java.util.Set;

public abstract class AbstractSystem implements GameSystem {
    protected final ComponentFactoryManager cfm;
    protected final ComponentManager cm;

    @Getter
    @Setter
    private Set<Class<? extends Component>> requiredComponents;

    protected AbstractSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        this.cfm = cfm;
        this.cm = cm;
    }

    @Override
    public void update() {
        Set<Long> targets = cm.getEntitiesForSystem(this);

        for (long id : targets) {
            updateEntity(id);
        }
    }

    protected abstract void updateEntity(long id);

    protected abstract double getBoost();
}
