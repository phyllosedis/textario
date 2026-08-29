package ru.phyllosedis.textario.engine.spring;


import ru.phyllosedis.textario.engine.ecs.entity.AbstractEntityFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Component
public class EntityFactoryRegistry {
    private final Map<Class<? extends AbstractEntityFactory>, AbstractEntityFactory> registry = new ConcurrentHashMap<>();

    public EntityFactoryRegistry(List<AbstractEntityFactory> list) {
        for (AbstractEntityFactory factory : list) {
            registry.put(factory.getClass(), factory);
        }
    }

    @SuppressWarnings("unchecked")
    public <F extends AbstractEntityFactory> F get(Class<F> markerClazz) {
        AbstractEntityFactory factory = registry.get(markerClazz);
        if (factory == null) {
            throw new IllegalArgumentException("Фабрика для маркера " + markerClazz.getSimpleName() + " не найдена.");
        }
        return (F) factory;
    }
}
