package ru.phyllosedis.textario.component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ComponentManager {

    private final Map<Class<?>, Map<Long, Object>> store = new HashMap<>();

    public <T> void add(long entityId, T component) {
        store.computeIfAbsent(component.getClass(), k -> new HashMap<>())
                .put(entityId, component);
    }

    public <T extends ru.phyllosedis.textario.component.Component> T get(long entityId, Class<T> componentClass) {
        Map<Long, Object> instances = store.get(componentClass);
        if (instances == null) {
            return null;
        }
        return (T) instances.get(entityId);
    }

    public boolean has(long entityId, Class<?> componentClass) {
        Map<Long, Object> instances = store.get(componentClass);
        return instances != null && instances.containsKey(entityId);
    }

    public void remove(long entityId, Class<?> componentClass) {
        Map<Long, Object> instances = store.get(componentClass);
        if (instances != null) {
            instances.remove(entityId);
        }
    }
}
