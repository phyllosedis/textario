package ru.phyllosedis.textario;

import com.tngtech.archunit.junit.AnalyzeClasses;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class ArchitectureSelfTest {
    @Test
    void all_concrete_architecture_tests_must_have_AnalyzeClasses_annotation() {
        Reflections reflections = new Reflections("ru.phyllosedis.textario");
        Set<Class<? extends AbstractArchitectureTest>> subTypes =
                reflections.getSubTypesOf(AbstractArchitectureTest.class);

        for (Class<?> clazz : subTypes) {
            if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            Annotation annotation = clazz.getAnnotation(AnalyzeClasses.class);
            if (annotation == null) {
                fail(String.format("Класс %s не помечен @AnalyzeClasses", clazz.getName()));
            }
        }
    }
}
