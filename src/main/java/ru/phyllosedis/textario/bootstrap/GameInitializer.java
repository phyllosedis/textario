package ru.phyllosedis.textario.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.console.GameCommands;
import ru.phyllosedis.textario.console.MapRenderer;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;

@Service
@Order(1)
@RequiredArgsConstructor
public class GameInitializer implements CommandLineRunner {

    private final EntityBlueprintService entityBlueprintService;
    private final GameCommands gameCommands;
    private final ComponentManager cm;
    private final ComponentFactoryRegistry cfm;
    private final MapRenderer mapRenderer;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== TEXTARIO: демо-линия железо -> печь -> плиты ===");
        buildDemo();
        System.out.println(mapRenderer.render(0, 15, 25, 20));
    }

    private void buildDemo() {
        // Железная линия: бур -> рука -> сундук -> лента -> рука -> печь -> рука -> сундук
        safe(() -> entityBlueprintService.createMiner(5, 20, Tier.ONE, ResourceType.IRON_ORE));
        safe(() -> entityBlueprintService.createInserter(5, 22, Tier.ONE, ResourceType.EARTH, 0));
        safe(() -> entityBlueprintService.createChest(5, 23, Tier.ONE));
        safe(() -> entityBlueprintService.createBelt(5, 24, Tier.ONE, ResourceType.EARTH, 0));
        safe(() -> entityBlueprintService.createInserter(5, 25, Tier.ONE, ResourceType.EARTH, 0));
        safe(() -> entityBlueprintService.createFurnace(5, 26, Tier.ONE));
        // Стартовый уголь в печь, чтобы демо не встало без топлива
        try {
            Long furnace = gameCommands.entityAt(5, 26);
            if (furnace != null) {
                cm.add(furnace, cfm.create(new InventoryComponent.Args(4, 50,
                        java.util.List.of(new InventoryComponent.ReadableSlot(ResourceType.COAL, 20)))));
            }
        } catch (Exception e) {
            System.out.println("[init] без угля: " + e.getMessage());
        }
        safe(() -> entityBlueprintService.createInserter(5, 28, Tier.ONE, ResourceType.EARTH, 0));
        safe(() -> entityBlueprintService.createChest(5, 29, Tier.ONE));

        // Сборка шестерёнок из плит: сундук -> рука -> сборщик -> рука -> сундук
        safe(() -> entityBlueprintService.createInserter(5, 30, Tier.ONE, ResourceType.EARTH, 0));
        safe(() -> entityBlueprintService.createAssembler(5, 31, Tier.ONE));
        safe(() -> entityBlueprintService.createInserter(5, 33, Tier.ONE, ResourceType.EARTH, 0));
        safe(() -> entityBlueprintService.createChest(5, 34, Tier.ONE));
        System.out.println("[init] " + gameCommands.setRecipe("5:31", "шестерёнки"));

        // Медная линия: бур -> рука -> сундук
        safe(() -> entityBlueprintService.createMiner(12, 25, Tier.ONE, ResourceType.COPPER_ORE));
        safe(() -> entityBlueprintService.createInserter(12, 27, Tier.ONE, ResourceType.EARTH, 0));
        safe(() -> entityBlueprintService.createChest(12, 28, Tier.ONE));

        // Разделитель для витрины (вне линии)
        safe(() -> entityBlueprintService.createSplitter(9, 18, Tier.ONE, ResourceType.EARTH, SplitMode.ROUND_ROBIN));
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            System.out.println("[init] пропуск постройки: " + e.getMessage());
        }
    }
}
