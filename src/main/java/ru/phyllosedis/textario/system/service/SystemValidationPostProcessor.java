package ru.phyllosedis.textario.system.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.impl.meta.tier.BuildingTier;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.system.Requires;

import java.util.HashSet;
import java.util.Set;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class SystemValidationPostProcessor implements BeanPostProcessor {

    private final ComponentManager cm;

    @Override
    public @Nullable Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Нас интересуют только бины, наследующиеся от нашей AbstractSystem
        if (bean instanceof AbstractSystem system) {
            Class<?> clazz = system.getClass();

            // 1. Собираем аннотации по всей цепочке наследования
            Set<Class<? extends ru.phyllosedis.textario.component.Component>> required = collectComponents(clazz);

            // 2. Валидация №1: Проверяем, что аннотации вообще есть
            if (required.isEmpty()) {
                throw new IllegalStateException(String.format(
                        "Критическая ошибка! Система %s или её предки обязаны иметь аннотацию @Requires.",
                        clazz.getSimpleName()
                ));
            }

            // 3. Валидация №2: Проверяем уникальность маркера тира
            validateTierUniqueness(clazz, required);

            // 4. Прокидываем собранный отвалидированный сет в систему
            system.setRequiredComponents(required);

            // 5. Регистрируем систему в реактивном кэше
            cm.registerSystem(system);
        }
        return bean;
    }

    private Set<Class<? extends Component>> collectComponents(Class<?> startClass) {
        Set<Class<? extends ru.phyllosedis.textario.component.Component>> components = new HashSet<>();
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

    private void validateTierUniqueness(Class<?> clazz, Set<Class<? extends ru.phyllosedis.textario.component.Component>> components) {
        long tierCount = components.stream()
                .filter(BuildingTier.class::isAssignableFrom)
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
    }

}
