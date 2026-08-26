package ru.phyllosedis.textario.component.factory;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.type.ComponentType;

@Component
public class AutoFactoryRegistrationProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(AutoFactory.class));

        for (var beanDefinition : scanner.findCandidateComponents("ru.phyllosedis.textario.component")) {
            try {
                Class<?> componentClass = Class.forName(beanDefinition.getBeanClassName());

                // 1. Вытаскиваем рекорд Args
                Class<?> argsClass = getAClass(componentClass);

                // 2. Безопасно достаем тип из аннотации БЕЗ парсинга строк
                AutoFactory annotation = componentClass.getAnnotation(AutoFactory.class);
                ComponentType componentType = annotation.value();

                // 3. Создаем бин динамической фабрики
                RootBeanDefinition factoryDefinition = new RootBeanDefinition(DynamicComponentFactory.class);
                factoryDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, componentType);
                factoryDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, argsClass);

                String beanName = componentClass.getSimpleName() + "AutoFactory";
                registry.registerBeanDefinition(beanName, factoryDefinition);

            } catch (Exception e) {
                throw new IllegalStateException("Ошибка авто-генерации фабрики для " + beanDefinition.getBeanClassName(), e);
            }
        }
    }

    private static @NonNull Class<?> getAClass(Class<?> componentClass) {
        Class<?> argsClass = null;
        for (Class<?> declaredClass : componentClass.getDeclaredClasses()) {
            if (declaredClass.isRecord() && ComponentArgs.class.isAssignableFrom(declaredClass)) {
                argsClass = declaredClass;
                break;
            }
        }

        if (argsClass == null) {
            throw new IllegalStateException("Внутри " + componentClass.getSimpleName() + " не найден рекорд Args!");
        }
        return argsClass;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinitionRegistryPostProcessor.super.postProcessBeanFactory(beanFactory);
    }
}
