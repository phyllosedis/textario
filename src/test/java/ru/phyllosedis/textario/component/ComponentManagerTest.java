package ru.phyllosedis.textario.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.inventory.InventoryComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.InserterComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierComponent;
import ru.phyllosedis.textario.component.impl.position.PositionComponent;
import ru.phyllosedis.textario.type.ResourceType;
import ru.phyllosedis.textario.type.Tier;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("Тесты ComponentManager")
public class ComponentManagerTest {

    private ComponentManager cm;

    @Autowired
    private ComponentFactoryManager cfm;

    private final long entityId = 1L;

    @BeforeEach
    void setUp() {
        cm = new ComponentManager();
    }

    @Test
    @DisplayName("Сущность соответствует системе, если есть все требуемые компоненты")
    void entityMatchesSystemWhenItHasAllRequiredComponents() {
        cm.add(entityId, cfm.create(new InserterComponent.Args(1.0, 1, 1)));
        cm.add(entityId, cfm.create(new OperationFinishedMarkerComponent.Args()));
        cm.add(entityId, cfm.create(new TierComponent.Args(Tier.ONE)));

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierComponent.class
        );

        assertTrue(
                cm.matchesSystemFilter(entityId, required),
                "Сущность должна подходить системе, так как содержит все требуемые компоненты"
        );
    }

    @Test
    @DisplayName("Сущность НЕ соответствует системе, если отсутствует один обязательный компонент")
    void entityDoesNotMatchSystemWhenOneRequiredComponentIsMissing() {
        cm.add(entityId, cfm.create(new InserterComponent.Args(1.0, 1, 1)));
        cm.add(entityId, cfm.create(new TierComponent.Args(Tier.ONE)));
        // OperationFinishedMarkerComponent отсутствует

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(entityId, required),
                "Сущность НЕ должна подходить системе, так как отсутствует OperationFinishedMarkerComponent"
        );
    }

    @Test
    @DisplayName("Сущность НЕ соответствует системе, если нет ни одного требуемого компонента")
    void entityDoesNotMatchSystemWhenItHasNoneOfRequiredComponents() {
        cm.add(entityId, cfm.create(new PositionComponent.Args(10, 20)));

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(entityId, required),
                "Сущность НЕ должна подходить системе, так как не содержит ни одного требуемого компонента"
        );
    }

    @Test
    @DisplayName("Сущность НЕ соответствует системе, если содержит только один из трёх обязательных компонентов")
    void entityDoesNotMatchSystemWhenItHasOnlyOneRequiredComponent() {
        cm.add(entityId, cfm.create(new InserterComponent.Args(1.0, 1, 1)));

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(entityId, required),
                "Сущность НЕ должна подходить системе, так как содержит только InserterComponent, а нужны все три"
        );
    }

    @Test
    @DisplayName("Сущность НЕ соответствует системе, если есть лишние компоненты, но отсутствует обязательный")
    void entityDoesNotMatchSystemWhenItHasExtraComponentsButMissingRequiredOne() {
        cm.add(entityId, cfm.create(new InserterComponent.Args(1.0, 1, 1)));
        cm.add(entityId, cfm.create(new TierComponent.Args(Tier.ONE)));
        cm.add(entityId, cfm.create(new PositionComponent.Args(10, 20)));
        cm.add(entityId, cfm.create(new InventoryComponent.Args(5, 1, List.of(new InventoryComponent.ReadableSlot(ResourceType.UNDEFINED, 1)))));

        Set<Class<? extends Component>> required = Set.of(
                InserterComponent.class,
                OperationFinishedMarkerComponent.class,
                TierComponent.class
        );

        assertFalse(
                cm.matchesSystemFilter(entityId, required),
                "Сущность НЕ должна подходить системе, так как отсутствует OperationFinishedMarkerComponent, несмотря на наличие лишних компонентов"
        );
    }
}
