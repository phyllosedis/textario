package ru.phyllosedis.textario.system;

import lombok.Getter;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSystem implements GameSystem {
    protected final ComponentFactoryManager cfm;
    protected final ComponentManager cm;

    @Getter
    private final Set<Class<? extends Component>> requiredComponents;

    protected AbstractSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        this.cfm = cfm;
        this.cm = cm;

        this.requiredComponents = collectRequiredComponents();

        this.cm.registerSystem(this);

        // Если вообще никто в цепочке не объявил аннотацию, кидаем ошибку
        if (this.requiredComponents.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "Критическая ошибка! Система %s или её предки обязаны иметь аннотацию @Requires " +
                            "с перечислением необходимых компонентов.",
                    this.getClass().getSimpleName()
            ));
        }
    }

    @Override
    public void update() {
        Set<Long> targets = cm.getEntitiesForSystem(this);

        for (long id : targets) {
            updateEntity(id);
        }
    }

    /**
     * Рекурсивно или в цикле обходит дерево предков и собирает все классы компонентов
     */
    private Set<Class<? extends Component>> collectRequiredComponents() {
        Set<Class<? extends Component>> components = new HashSet<>();
        Class<?> currentClass = this.getClass();
        while (currentClass != null && currentClass != Object.class) {
            Requires requiresAnn = currentClass.getAnnotation(Requires.class);
            if (requiresAnn != null) {
                components.addAll(Set.of(requiresAnn.value()));
            }
            currentClass = currentClass.getSuperclass();
        }
        return Set.copyOf(components);
    }

    protected abstract void updateEntity(long id);

    protected abstract double getBoost();
}
