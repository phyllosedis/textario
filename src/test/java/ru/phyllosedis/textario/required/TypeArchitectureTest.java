package ru.phyllosedis.textario.required;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import ru.phyllosedis.textario.resource.Type;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "ru.phyllosedis.textario")
public class TypeArchitectureTest {

    @ArchTest
    static final ArchRule allEnumsShouldImplementType =
            classes()
                    .that().areEnums()
                    .should().implement(Type.class);
}
