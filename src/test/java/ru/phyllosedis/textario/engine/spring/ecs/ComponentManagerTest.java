package ru.phyllosedis.textario.engine.spring.ecs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.logistics.inserter.InserterComponent;
import ru.phyllosedis.textario.production.mining.MiningComponent;
import ru.phyllosedis.textario.production.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.PositionComponent;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тесты ComponentManager")
public class ComponentManagerTest {

    private ComponentManager cm;

    private final long entityId = 1L;

    @BeforeEach
    void setUp() {
        cm = new ComponentManager();
    }

    @Test
    @DisplayName("Сущность соответствует системе, если есть все требуемые компоненты")
    void entityMatchesSystemWhenItHasAllRequiredComponents() {
        cm.add(entityId, new InserterComponent.Args(1.0, 1, 1).instantiate());
        cm.add(entityId, new OperationFinishedMarkerComponent.Args().instantiate());
        cm.add(entityId, new TierMarkerComponent.Args().instantiate());

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierMarkerComponent.class
        );

        assertTrue(
                cm.matchesSystemFilter(entityId, required),
                "Сущность должна подходить системе, так как содержит все требуемые компоненты"
        );
    }

    @Test
    @DisplayName("Сущность НЕ соответствует системе, если отсутствует один обязательный компонент")
    void entityDoesNotMatchSystemWhenOneRequiredComponentIsMissing() {
        cm.add(entityId, new InserterComponent.Args(1.0, 1, 1).instantiate());
        cm.add(entityId, new TierMarkerComponent.Args().instantiate());
        // OperationFinishedMarkerComponent отсутствует

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierMarkerComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(entityId, required),
                "Сущность НЕ должна подходить системе, так как отсутствует OperationFinishedMarkerComponent"
        );
    }

    @Test
    @DisplayName("Сущность НЕ соответствует системе, если нет ни одного требуемого компонента")
    void entityDoesNotMatchSystemWhenItHasNoneOfRequiredComponents() {
        cm.add(entityId, new PositionComponent.Args(10, 20).instantiate());

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierMarkerComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(entityId, required),
                "Сущность НЕ должна подходить системе, так как не содержит ни одного требуемого компонента"
        );
    }

    @Test
    @DisplayName("Сущность НЕ соответствует системе, если содержит только один из трёх обязательных компонентов")
    void entityDoesNotMatchSystemWhenItHasOnlyOneRequiredComponent() {
        cm.add(entityId, new InserterComponent.Args(1.0, 1, 1).instantiate());

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierMarkerComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(entityId, required),
                "Сущность НЕ должна подходить системе, так как содержит только InserterComponent, а нужны все три"
        );
    }

    @Test
    @DisplayName("Сущность НЕ соответствует системе, если есть лишние компоненты, но отсутствует обязательный")
    void entityDoesNotMatchSystemWhenItHasExtraComponentsButMissingRequiredOne() {
        cm.add(entityId, new InserterComponent.Args(1.0, 1, 1).instantiate());
        cm.add(entityId, new TierMarkerComponent.Args().instantiate());
        cm.add(entityId, new PositionComponent.Args(10, 20).instantiate());
        cm.add(entityId, new InventoryComponent.Args(5, 1, List.of(new InventoryComponent.ReadableSlot(ResourceType.UNDEFINED, 1))).instantiate());

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierMarkerComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(entityId, required),
                "Сущность НЕ должна подходить системе, так как отсутствует OperationFinishedMarkerComponent, несмотря на наличие лишних компонентов"
        );
    }

    @Test
    @DisplayName("Добытчик НЕ должен попадать в кэш системы Манипулятора (у него отсутствуют соответствующие компоненты:  InserterComponent и OperationFinishedMarkerComponent)")
    void minerMustNotBeAddedToInserterSystemCache() {
        cm.add(entityId, new PositionComponent.Args(10, 20).instantiate());
        cm.add(entityId, new BuildingComponent.Args(1, 2).instantiate());
        cm.add(entityId, new MiningComponent.Args(ResourceType.UNDEFINED).instantiate());
        cm.add(entityId, new InventoryComponent.Args(5, 1, List.of(new InventoryComponent.ReadableSlot(ResourceType.UNDEFINED, 1))).instantiate());
        cm.add(entityId, new TierMarkerComponent.Args().instantiate());


        Set<Class<? extends Component>> inserterRequirements = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierMarkerComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(
                        entityId,
                        inserterRequirements
                ),
                "Добытчик НЕ должен попадать в кэш системы Манипулятора (у него отсутствуют соответствующие компоненты:  InserterComponent и OperationFinishedMarkerComponent)"
        );
    }

    @MethodSource("missingComponents")
    @DisplayName("Сущность НЕ соответствует системе при удалении любого из обязательных компонентов")
    @ParameterizedTest(name = "При отсутствии компонента {0} сущность перестаёт соответствовать системе")
    void entityDoesNotMatchWhenAnyRequiredComponentIsMissing(
            Class<? extends Component> missingComponent
    ) {
        cm.add(entityId, new InserterComponent.Args(1.0, 1, 1).instantiate());
        cm.add(entityId, new OperationFinishedMarkerComponent.Args().instantiate());
        cm.add(entityId, new TierMarkerComponent.Args().instantiate());

        cm.remove(entityId, missingComponent);

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierMarkerComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(entityId, required),
                "После удаления " + missingComponent.getSimpleName() +
                        " сущность перестаёт соответствовать системе, так как не хватает этого компонента"
        );
    }

    static Stream<Class<? extends Component>> missingComponents() {
        return Stream.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierMarkerComponent.class
        );
    }
}
