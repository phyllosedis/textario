package ru.phyllosedis.textario.balance;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.AbstractArchitectureTest;

import java.lang.annotation.Annotation;
import java.util.List;

@AnalyzeClasses(packages = "ru.phyllosedis.textario.balance")
public class BalanceArchitectureTest extends AbstractArchitectureTest {

    @ArchTest
    static final ArchRule RULE =
            new BalanceArchitectureTest().getCheckRule();


    @Override
    protected Class<?> getDomainClass() {
        return AbstractBalance.class;
    }

    private final List<Class<? extends Annotation>> BALANCE_REQUIRED = List.of(
            Component.class
    );

    @Override
    protected List<Class<? extends Annotation>> getRequiredAnnotations() {
        return BALANCE_REQUIRED;
    }

}
