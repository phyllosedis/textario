package ru.phyllosedis.textario.system;

import lombok.Getter;
import ru.phyllosedis.textario.Entity;
import ru.phyllosedis.textario.GameInitializer;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.type.Grade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractSystem implements GameSystem {
    protected final ComponentFactoryManager cfm;
    protected final ComponentManager cm;
    protected final Grade grade;

    @Getter
    private final Set<Class<? extends Component>> requiredComponents;

    protected AbstractSystem(ComponentFactoryManager cfm, ComponentManager cm, Grade grade) {
        this.cfm = cfm;
        this.cm = cm;
        this.grade = grade;

        this.requiredComponents = collectRequiredComponents();

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
        List<Entity> activeEntities = GameInitializer.activeEntities;
        for (int i = 0; i < activeEntities.size(); i++) {
            long id = activeEntities.get(i).getId();
            if (matchesFilter(id)) {
                updateEntity(id);
            }
        }
    }

    private boolean matchesFilter(long id) {
        for (Class<? extends Component> componentClass : requiredComponents) {
            if (!cm.has(id, componentClass)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Рекурсивно или в цикле обходит дерево предков и собирает все классы компонентов
     */
    private Set<Class<? extends Component>> collectRequiredComponents() {
        Set<Class<? extends Component>> components = new HashSet<>();
        Class<?> currentClass = this.getClass();

        // Идем вверх по иерархии, пока не упремся в Object или AbstractSystem
        while (currentClass != null && currentClass != Object.class) {
            Requires requiresAnn = currentClass.getAnnotation(Requires.class);
            if (requiresAnn != null) {
                // Добавляем все компоненты из текущей аннотации в общий сет
                components.addAll(Set.of(requiresAnn.value()));
            }
            currentClass = currentClass.getSuperclass();
        }

        return Set.copyOf(components); // Возвращаем неизменяемый сет
    }

    protected abstract void updateEntity(long id);
}
