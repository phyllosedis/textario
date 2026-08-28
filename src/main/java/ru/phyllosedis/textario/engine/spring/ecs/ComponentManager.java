package ru.phyllosedis.textario.engine.spring.ecs;

import lombok.RequiredArgsConstructor;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.system.AbstractSystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class ComponentManager {

    // Основное хранилище ECS: Сущность -> (Класс Компонента -> Сам Компонент)
    private final Map<Long, Map<Class<? extends Component>, Component>> storage = new ConcurrentHashMap<>();

    // Реактивный кэш: Система -> Сет ID сущностей, которые ей подходят
    private final Map<AbstractSystem, Set<Long>> systemCache = new ConcurrentHashMap<>();

    // Список всех зарегистрированных в игре систем
    private final List<AbstractSystem> registeredSystems = new ArrayList<>();

    // Регистрация системы (вызывается один раз при старте игры)
    public void registerSystem(AbstractSystem system) {
        registeredSystems.add(system);
        systemCache.put(system, ConcurrentHashMap.newKeySet());
    }

    // Возвращает предвычисленный кэш для конкретной системы за O(1)
    public Set<Long> getEntitiesForSystem(AbstractSystem system) {
        return systemCache.getOrDefault(system, Collections.emptySet());
    }

    public void add(long id, Component component) {
        storage.computeIfAbsent(id, k -> new ConcurrentHashMap<>()).put(component.getClass(), component);
        updateEntityInCache(id); // РЕАКЦИЯ: Изменился состав компонентов
    }

    public void remove(long id, Class<? extends Component> componentClass) {
        Map<Class<? extends Component>, Component> entityComponents = storage.get(id);
        if (entityComponents != null) {
            entityComponents.remove(componentClass);
            if (entityComponents.isEmpty()) {
                storage.remove(id);
            }
        }
        updateEntityInCache(id); // РЕАКЦИЯ: Компонент удален
    }

    public boolean has(long id, Class<? extends Component> componentClass) {
        Map<Class<? extends Component>, Component> entityComponents = storage.get(id);
        return entityComponents != null && entityComponents.containsKey(componentClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T get(long id, Class<T> componentClass) {
        Map<Class<? extends Component>, Component> entityComponents = storage.get(id);
        return entityComponents != null ? (T) entityComponents.get(componentClass) : null;
    }

    /**
     * Главный реактивный метод. Пересчитывает вхождение сущности в системы.
     * Работает за O(K), где K — количество систем (обычно их немного, до 50-100 штук),
     * вместо O(N) по тысячам сущностей в игровом цикле.
     */
    private void updateEntityInCache(long id) {
        for (AbstractSystem system : registeredSystems) {
            if (matchesSystemFilter(id, system.getRequiredComponents())) {
                systemCache.get(system).add(id);     // Добавляем в кэш системы за O(1)
            } else {
                systemCache.get(system).remove(id);  // Удаляем из кэша системы за O(1)
            }
        }
    }

    protected boolean matchesSystemFilter(
            long id,
            Set<Class<? extends Component>> required
    ) {
        return required != null
                && !required.isEmpty()
                && required.stream().allMatch(component -> has(id, component));
    }
}
