package ru.phyllosedis.textario.service.factory;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.AbstractArchitectureTest;
import ru.phyllosedis.textario.service.factory.marker.AssociatedMarker;

import java.lang.annotation.Annotation;
import java.util.List;

@AnalyzeClasses(packages = "ru.phyllosedis.textario.service.factory")
public class FactoryArchitectureTest extends AbstractArchitectureTest {

    @ArchTest
    static final ArchRule RULE =
            new FactoryArchitectureTest().getCheckRule();

    private final List<Class<? extends Annotation>> ABSTRACT_ENTITY_FACTORY_REQUIRED = List.of(
            Component.class,
            AssociatedMarker.class
    );

    @Override
    protected List<Class<? extends Annotation>> getRequiredAnnotations() {
        return ABSTRACT_ENTITY_FACTORY_REQUIRED;
    }

    @Override
    protected Class<?> getDomainClass() {
        return AbstractEntityFactory.class;
    }
}
