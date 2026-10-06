package ru.phyllosedis.textario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.phyllosedis.textario.bootstrap.TextarioApplication;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TextarioApplication.class, properties = "textario.console.enabled=false")
@DisplayName("Прототип: цепочки добычи и плавки")
class PrototypeChainTest {

    @Autowired
    private EntityBlueprintService blueprints;

    @Autowired
    private ComponentManager cm;

    @Autowired
    private ComponentFactoryRegistry cfm;

    @Test
    @DisplayName("Уголь доезжает из бура в сундук через манипулятор")
    void coalReachesChest() throws Exception {
        long miner = blueprints.createMiner(18, 20, Tier.ONE, ResourceType.COAL);
        blueprints.createInserter(18, 22, Tier.ONE, ResourceType.EARTH, 0);
        long chest = blueprints.createChest(18, 23, Tier.ONE);

        Thread.sleep(6000);

        InventoryComponent inv = cm.get(chest, InventoryComponent.class);
        int coal = inv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.COAL)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] сундук #" + chest + " уголь: " + coal + " (бур #" + miner + ")");
        assertTrue(coal > 0, "в сундуке должен быть уголь, добытый буром #" + miner);
    }

    @Test
    @DisplayName("Печь плавит руду в плиты")
    void furnaceSmeltsPlates() throws Exception {
        long furnace = blueprints.createFurnace(30, 30, Tier.ONE);
        cm.add(furnace, cfm.create(new InventoryComponent.Args(4, 50,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.IRON_ORE, 5)))));

        Thread.sleep(6000);

        InventoryComponent inv = cm.get(furnace, InventoryComponent.class);
        int plates = inv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.IRON_PLATE)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] печь #" + furnace + " плиты: " + plates);
        assertTrue(plates > 0, "в печи должны быть железные плиты");
    }

    @Test
    @DisplayName("Повёрнутая рука везёт вбок")
    void rotatedInserterMovesSideways() throws Exception {
        long src = blueprints.createChest(40, 22, Tier.ONE);
        cm.add(src, cfm.create(new InventoryComponent.Args(8, 100,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.COAL, 3)))));
        // Поворот 3: вход смотрит влево (40:22), выход — вправо (42:22)
        blueprints.createInserter(41, 22, Tier.ONE, ResourceType.EARTH, 3);
        long dst = blueprints.createChest(42, 22, Tier.ONE);

        Thread.sleep(3000);

        InventoryComponent inv = cm.get(dst, InventoryComponent.class);
        int coal = inv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.COAL)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] сундук-приёмник #" + dst + " уголь: " + coal);
        assertTrue(coal > 0, "повёрнутая рука должна перевезти уголь вбок");
    }
}
