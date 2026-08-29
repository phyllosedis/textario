package ru.phyllosedis.textario.engine.spring.ecs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.ComponentFactory;

import java.util.List;

@Configuration
public class EcsConfiguration {

    @Bean
    public ComponentManager componentManager() {
        return new ComponentManager();
    }

    @Bean
    public ComponentFactoryRegistry componentFactoryRegistry(
            List<ComponentFactory<?, ?>> factories
    ) {
        return new ComponentFactoryRegistry(factories);
    }
}
