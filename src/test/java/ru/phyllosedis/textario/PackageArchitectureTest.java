package ru.phyllosedis.textario;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "ru.phyllosedis.textario")
public class PackageArchitectureTest extends AbstractArchitectureTest {

    @ArchTest
    static final ArchRule belt_classes_belong_to_belt_package =
            classes()
                    .that()
                    .haveSimpleNameContaining("Belt")
                    .should()
                    .resideInAnyPackage("..logistics.belt..");

    @ArchTest
    static final ArchRule inserter_classes_belong_to_inserter_package =
            classes()
                    .that()
                    .haveSimpleNameContaining("Inserter")
                    .should()
                    .resideInAnyPackage("..logistics.inserter..");

    @ArchTest
    static final ArchRule splitter_classes_belong_to_splitter_package =
            classes()
                    .that()
                    .haveSimpleNameContaining("Splitter")
                    .should()
                    .resideInAnyPackage("..logistics.splitter..");

    @ArchTest
    static final ArchRule mining_classes_belong_to_mining_package =
            classes()
                    .that()
                    .haveSimpleNameContaining("Mining")
                    .or()
                    .haveSimpleNameContaining("Miner")
                    .should()
                    .resideInAnyPackage("..production.mining..");


    @Override
    protected List<Class<? extends Annotation>> getRequiredAnnotations() {
        return List.of();
    }

    @Override
    protected Class<?> getDomainClass() {
        return null;
    }
}
