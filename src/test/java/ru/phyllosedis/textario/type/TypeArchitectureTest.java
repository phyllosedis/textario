package ru.phyllosedis.textario.type;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "ru.phyllosedis.textario.type")
public class TypeArchitectureTest {

    @ArchTest
    static final ArchRule allEnumsShouldImplementType =
            classes()
                    .that().areEnums()
                    .should().implement(Type.class);
}
