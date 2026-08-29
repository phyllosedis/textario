package ru.phyllosedis.textario.engine.spring;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;

import java.util.HashSet;
import java.util.Set;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class SystemValidationPostProcessor implements BeanPostProcessor {

    private final ConfigurableListableBeanFactory beanFactory;
    private final ComponentManager cm;

    @Override
    public @Nullable Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractSystem system) {
            Class<?> clazz = system.getClass();
            Set<Class<? extends Component>> required = new HashSet<>(collectComponents(clazz));

            if (required.isEmpty()) {
                throw new IllegalStateException(String.format(
                        "Критическая ошибка! Система %s или её предки обязаны иметь аннотацию @Requires.",
                        clazz.getSimpleName()
                ));
            }

//            validateTierUniqueness(clazz, required);
            system.setRequiredComponents(Set.copyOf(required));

            cm.registerSystem(system);
        }
        return bean;
    }

    private Set<Class<? extends Component>> collectComponents(Class<?> startClass) {
        Set<Class<? extends Component>> components = new HashSet<>();
        Class<?> currentClass = startClass;

        while (currentClass != null && currentClass != Object.class) {
            Requires requiresAnn = currentClass.getAnnotation(Requires.class);
            if (requiresAnn != null) {
                components.addAll(Set.of(requiresAnn.value()));
            }
            currentClass = currentClass.getSuperclass();
        }
        return Set.copyOf(components);
    }
/*
    private void validateTierUniqueness(Class<?> clazz, Set<Class<? extends Component>> components) {
        long tierCount = components.stream()
                .filter(TierMarkerComponent.class::isAssignableFrom)
                .count();

        // Пропускаем абстрактные промежуточные классы вроде самой MiningSystem, если они всплывут в контексте
        if (tierCount == 0 && !clazz.getSimpleName().endsWith("MiningSystem")) {
            throw new IllegalStateException(String.format(
                    "Критическая ошибка в %s! Система обязана требовать ровно один маркер тира (BuildingTier). Найдено: 0",
                    clazz.getSimpleName()
            ));
        }

        if (tierCount > 1) {
            throw new IllegalStateException(String.format(
                    "Критическая ошибка в %s! Запрещено вешать на систему более одного маркера тира одновременно! Найдено тиров: %d",
                    clazz.getSimpleName(), tierCount
            ));
        }
    }*/

}
