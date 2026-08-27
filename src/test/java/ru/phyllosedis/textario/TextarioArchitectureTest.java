package ru.phyllosedis.textario;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.system.belt.BeltSystem;
import ru.phyllosedis.textario.system.inserter.InserterSystem;
import ru.phyllosedis.textario.system.mining.MiningResourceSystem;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@AnalyzeClasses(packages = "ru.phyllosedis.textario.system")
class TextarioArchitectureTest {

    private static final List<Class<? extends AbstractSystem>> EXECUTION_PIPELINE = List.of(
            MiningResourceSystem.class,
            InserterSystem.class,
            BeltSystem.class
    );


    @ArchTest
    static void execution_pipeline_is_in_correct_order(JavaClasses classes) {
        for (int i = 0; i < EXECUTION_PIPELINE.size() - 1; i++) {
            Class<? extends AbstractSystem> previous = EXECUTION_PIPELINE.get(i);
            Class<? extends AbstractSystem> next = EXECUTION_PIPELINE.get(i + 1);

            int previousOrder = getOrder(classes, previous);
            int nextOrder = getOrder(classes, next);

            assertTrue(
                    previousOrder < nextOrder,
                    String.format(
                            "%s (@Order(%d)) должен выполняться раньше %s (@Order(%d))",
                            next.getSimpleName(),
                            nextOrder,
                            previous.getSimpleName(),
                            previousOrder
                    )
            );
        }
    }

    private static int getOrder(
            JavaClasses classes,
            Class<? extends AbstractSystem> systemType
    ) {
        List<JavaClass> systems = classes.stream()
                .filter(e -> e.isAnnotatedWith(Component.class))
                .filter(javaClass ->
                        javaClass.isAssignableTo(systemType)
                                && !javaClass.getModifiers().contains(JavaModifier.ABSTRACT)
                )
                .toList();

        assertFalse(
                systems.isEmpty(),
                "Не найдено ни одной concrete-системы для "
                        + systemType.getSimpleName()
        );

        Set<Integer> orders = systems.stream()
                .map(javaClass ->
                        javaClass.getAnnotationOfType(Order.class).value()
                )
                .collect(Collectors.toSet());

        assertEquals(
                1,
                orders.size(),
                "Все системы этапа " + systemType.getSimpleName()
                        + " должны иметь одинаковый @Order"
        );

        return orders.iterator().next();
    }

    private static int getStageOrder(
            JavaClasses classes,
            Class<? extends AbstractSystem> stage
    ) {
        List<JavaClass> systems = classes.stream()
                .filter(e -> e.isAnnotatedWith(Component.class))
                .filter(javaClass ->
                        javaClass.isAssignableTo(stage)
                                && !javaClass.getModifiers().contains(JavaModifier.ABSTRACT)
                )
                .toList();

        assertFalse(
                systems.isEmpty(),
                "Не найдена ни одна concrete-система для "
                        + stage.getSimpleName()
        );

        Set<Integer> orders = systems.stream()
                .map(javaClass ->
                        javaClass.getAnnotationOfType(Order.class).value()
                )
                .collect(Collectors.toSet());

        assertEquals(
                1,
                orders.size(),
                "Все системы этапа " + stage.getSimpleName()
                        + " должны иметь одинаковый @Order"
        );

        return orders.iterator().next();
    }
}
