package ru.phyllosedis.textario;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public abstract class AbstractArchitectureTest {

    protected abstract List<Class<? extends Annotation>> getRequiredAnnotations();

    protected abstract Class<?> getDomainClass();

    public ArchRule getCheckRule() {
        return classes()
                .that().areAssignableTo(getDomainClass())
                .and().doNotHaveModifier(JavaModifier.ABSTRACT)
                .should(haveAllAnnotations(getRequiredAnnotations()));
    }

    private static ArchCondition<JavaClass> haveAllAnnotations(List<Class<? extends Annotation>> annotations) {
        return new ArchCondition<>("быть помеченным всеми аннотациями из списка: " + annotations) {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (Class<? extends Annotation> annotation : annotations) {
                    if (!javaClass.isAnnotatedWith(annotation)) {
                        String message = String.format("Класс %s не помечен обязательной аннотацией @%s",
                                javaClass.getName(), annotation.getSimpleName());
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                }
            }
        };
    }
}
