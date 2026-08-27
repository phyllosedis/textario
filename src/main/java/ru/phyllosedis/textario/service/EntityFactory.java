package ru.phyllosedis.textario.service;


import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Component
public class EntityFactory {
    private final Map<Class<? extends AbstractEntityFactory>, AbstractEntityFactory> registry = new ConcurrentHashMap<>();

    public EntityFactory(List<AbstractEntityFactory> list) {
        for (AbstractEntityFactory factory : list) {
            registry.put(factory.getClass(), factory);
        }
        // TODO выстрелит если отказаться от AssociatedMarker.class
//        Map<String, AbstractEntityFactory> beans = context.getBeansOfType(AbstractEntityFactory.class);
//        for (AbstractEntityFactory factory : beans.values()) {
//            if (factory.getClass().isAnnotationPresent(AssociatedMarker.class)) {
//                Class<? extends MarkerComponent> marker = factory.getClass().getAnnotation(AssociatedMarker.class).value();
//                registry.put(marker, factory);
//            }
//        }
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
