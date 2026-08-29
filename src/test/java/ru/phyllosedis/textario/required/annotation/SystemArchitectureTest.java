package ru.phyllosedis.textario.required.annotation;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.AbstractArchitectureTest;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;

import java.lang.annotation.Annotation;
import java.util.List;

@AnalyzeClasses(packages = "ru.phyllosedis.textario")
public class SystemArchitectureTest extends AbstractArchitectureTest {
    @ArchTest
    static final ArchRule RULE =
            new SystemArchitectureTest().getCheckRule();


    private final List<Class<? extends Annotation>> ABSTRACT_SYSTEM_REQUIRED = List.of(
            Component.class,
            Order.class,
            Requires.class
    );

    @Override
    protected List<Class<? extends Annotation>> getRequiredAnnotations() {
        return List.of();
    }

    @Override
    protected Class<?> getDomainClass() {
        return AbstractSystem.class;
    }
}
