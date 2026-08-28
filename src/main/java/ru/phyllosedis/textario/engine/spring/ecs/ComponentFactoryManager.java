package ru.phyllosedis.textario.engine.spring.ecs;

import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComponentFactoryManager {
    private final Map<Class<?>, ComponentFactory<?, ?>> factories;

    public ComponentFactoryManager(List<ComponentFactory<?, ?>> factoryList) {
        this.factories = factoryList.stream()
                .collect(Collectors.toMap(ComponentFactory::getArgsClass, factory -> factory));
    }

    @SuppressWarnings("unchecked")
    public <C extends Component, A extends ComponentArgs<C>> C create(A args) {
        ComponentFactory<C, A> factory = (ComponentFactory<C, A>) factories.get(args.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("Фабрика для аргументов " + args.getClass().getSimpleName() + " не найдена!");
        }
        return factory.create(args);
    }
}
