package ru.phyllosedis.textario.required.annotation;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import ru.phyllosedis.textario.AbstractArchitectureTest;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.Component;

import java.lang.annotation.Annotation;
import java.util.List;

@AnalyzeClasses(packages = "ru.phyllosedis.textario")
public class ComponentArchitectureTest extends AbstractArchitectureTest {

    @ArchTest
    static final ArchRule RULE =
            new ComponentArchitectureTest().getCheckRule();

    private final List<Class<? extends Annotation>> COMPONENT_REQUIRED = List.of(
            AutoFactory.class
    );

    @Override
    protected List<Class<? extends Annotation>> getRequiredAnnotations() {
        return COMPONENT_REQUIRED;
    }

    @Override
    protected Class<?> getDomainClass() {
        return Component.class;
    }

    @Override
    protected ArchCondition<JavaClass> getAdditionalCondition() {
        return new ArchCondition<>("иметь маркер в @AssociatedMarker, который наследует Component и реализует MarkerComponent") {
            @Override
            public void check(JavaClass factoryClass, ConditionEvents events) {
                if (factoryClass.isAssignableTo(Component.class)) {
                    var annotationClass = AutoFactory.class;

                    boolean isAutoFactory = factoryClass.isAnnotatedWith(annotationClass);

                    if (!isAutoFactory) {
                        String message = String.format(
                                "Критическая ошибка! Компонент %s не обладает аннотацией %s:",
                                factoryClass.getSimpleName(), annotationClass
                        );
                        events.add(SimpleConditionEvent.violated(factoryClass, message));
                    }
                }
            }
        };
    }
}
