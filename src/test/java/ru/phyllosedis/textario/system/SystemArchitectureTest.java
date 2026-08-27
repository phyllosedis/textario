package ru.phyllosedis.textario.system;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.AbstractArchitectureTest;

import java.lang.annotation.Annotation;
import java.util.List;

@AnalyzeClasses(packages = "ru.phyllosedis.textario.system")
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
