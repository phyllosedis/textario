package ru.phyllosedis.textario.required.annotation;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.AbstractArchitectureTest;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.engine.ecs.component.MarkerComponent;
import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "ru.phyllosedis.textario")
public class FactoryArchitectureTest extends AbstractArchitectureTest {

    @ArchTest
    static final ArchRule RULE =
            new FactoryArchitectureTest().getCheckRule();

    private final List<Class<? extends Annotation>> ABSTRACT_ENTITY_FACTORY_REQUIRED = List.of(
            Component.class,
            AssociatedMarker.class
    );


    @Override
    protected GivenClassesConjunction getConjunction() {
        return classes()
                .that().areAssignableTo(getDomainClass())
                .and().areNotInterfaces()
                .and().doNotHaveModifier(JavaModifier.ABSTRACT);
    }

    @Override
    protected List<Class<? extends Annotation>> getRequiredAnnotations() {
        return ABSTRACT_ENTITY_FACTORY_REQUIRED;
    }

    @Override
    protected Class<?> getDomainClass() {
        return AbstractEntityFactory.class;
    }

    @Override
    protected ArchCondition<JavaClass> getAdditionalCondition() {
        return new ArchCondition<>("иметь маркер в @AssociatedMarker, который наследует Component и реализует MarkerComponent") {
            @Override
            public void check(JavaClass factoryClass, ConditionEvents events) {
                if (factoryClass.isAnnotatedWith(AssociatedMarker.class)) {
                    AssociatedMarker annotation = factoryClass.getAnnotationOfType(AssociatedMarker.class);
                    Class<?> markerClass = annotation.value();

                    boolean isComponent = ru.phyllosedis.textario.engine.ecs.component.Component.class.isAssignableFrom(markerClass);
                    boolean isMarker = MarkerComponent.class.isAssignableFrom(markerClass);

                    if (!isComponent || !isMarker) {
                        String message = String.format(
                                "Критическая ошибка! Фабрика %s указывает на маркер %s, который не удовлетворяет условиям: " +
                                        "[Наследует Component: %b, Реализует MarkerComponent: %b]",
                                factoryClass.getSimpleName(), markerClass.getSimpleName(), isComponent, isMarker
                        );
                        events.add(SimpleConditionEvent.violated(factoryClass, message));
                    }
                }
            }
        };
    }
}
