package ru.phyllosedis.textario.engine.ecs.system.futureuse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

//@Component
// TODO потенциальна полезна, при использовании изменить пакет
public class AutoSystemRegistrationPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(AutoSystemsConfiguration.class));

        for (var beanDefinition : scanner.findCandidateComponents("ru.phyllosedis.textario.system")) {
            try {
                Class<?> systemClass = Class.forName(beanDefinition.getBeanClassName());
                AutoSystemsConfiguration rootConfig = systemClass.getAnnotation(AutoSystemsConfiguration.class);

                // Цикл 1: Проходим по логическим группам зданий (SOLID, LIQUID и т.д.)
                for (TargetSystemConfig targetConfig : rootConfig.value()) {
                    Class<?> targetSystemClass = targetConfig.targetSystemClass();
                    String targetName = targetSystemClass.getSimpleName().replace("System", "");

                    // Цикл 2: Разворачиваем тиры для конкретного типа здания
                    int tierIndex = 1;
                    for (TierConfig tier : targetConfig.tiers()) {
                        RootBeanDefinition sysDef = new RootBeanDefinition(systemClass);

                        // Прокидываем boost, настроенный для этого тира
                        sysDef.getConstructorArgumentValues().addIndexedArgumentValue(2, tier.boost());
                        sysDef.getPropertyValues().add("order", tier.order());

                        // Генерируем красивое имя бина, например: ProductionProgressSystem_SolidMiningResource_Tier_2
                        String beanName = systemClass.getSimpleName() + "_" + targetName + "_Tier_" + tierIndex;
                        registry.registerBeanDefinition(beanName, sysDef);

                        // Сохраняем маркеры в метаданные для Спринг-постпроцессора
                        sysDef.setAttribute("tierMarker", tier.tierMarker());
                        sysDef.setAttribute("targetSystemClass", targetSystemClass);

                        tierIndex++;
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException("Ошибка авто-генерации иерархических систем", e);
            }
        }
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinitionRegistryPostProcessor.super.postProcessBeanFactory(beanFactory);
    }
}
