package ru.phyllosedis.textario.component.impl;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import ru.phyllosedis.textario.AbstractArchitectureTest;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;

import java.lang.annotation.Annotation;
import java.util.List;

@AnalyzeClasses(packages = "ru.phyllosedis.textario.component.impl")
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
}
