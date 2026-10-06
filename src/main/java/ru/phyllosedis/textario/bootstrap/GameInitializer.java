package ru.phyllosedis.textario.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.console.MapRenderer;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;

@Service
@Order(1)
@RequiredArgsConstructor
public class GameInitializer implements CommandLineRunner {

    private final EntityBlueprintService entityBlueprintService;
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
        safe(() -> entityBlueprintService.createInserter(5, 22, Tier.ONE, ResourceType.EARTH));
        safe(() -> entityBlueprintService.createChest(5, 23, Tier.ONE));
        safe(() -> entityBlueprintService.createBelt(5, 24, Tier.ONE, ResourceType.EARTH));
        safe(() -> entityBlueprintService.createInserter(5, 25, Tier.ONE, ResourceType.EARTH));
        safe(() -> entityBlueprintService.createFurnace(5, 26, Tier.ONE));
        safe(() -> entityBlueprintService.createInserter(5, 28, Tier.ONE, ResourceType.EARTH));
        safe(() -> entityBlueprintService.createChest(5, 29, Tier.ONE));

        // Медная линия: бур -> рука -> сундук
        safe(() -> entityBlueprintService.createMiner(12, 25, Tier.ONE, ResourceType.COPPER_ORE));
        safe(() -> entityBlueprintService.createInserter(12, 27, Tier.ONE, ResourceType.EARTH));
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
