package ru.phyllosedis.textario;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.inventory.InventoryComponent;
import ru.phyllosedis.textario.component.impl.meta.building.BuildingComponent;
import ru.phyllosedis.textario.component.impl.meta.tier.TierOneMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.tier.TierTwoMarkerComponent;
import ru.phyllosedis.textario.component.impl.mining.MiningComponent;
import ru.phyllosedis.textario.component.impl.position.PositionComponent;
import ru.phyllosedis.textario.map.PlacementService;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class GameInitializer implements CommandLineRunner {

    private final ComponentFactoryManager cfm;
    private final ComponentManager cm;
    public static final List<Entity> activeEntities = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final PlacementService placementService;


    private long tryBuildMiner(int x, int y, ResourceType targetResource, int tier) {
        // Бур занимает 1х1 клетку и обязан стоять на клетке с целевой рудой
        int width = 1;
        int height = 1;

        if (!placementService.canPlace(x, y, width, height, targetResource)) {
            System.out.println("❌ Не удалось установить бур на (" + x + "," + y + "): место занято или тип почвы не подходит!");
            return -1;
        }

        // Если валидация прошла — создаем сущность, как раньше
        long minerId = idGenerator.incrementAndGet();
        Entity minerEntity = new Entity(minerId);

        cm.add(minerId, cfm.create(new MiningComponent.Args(targetResource, 1, 0)));
        cm.add(minerId, cfm.create(new PositionComponent.Args(x, y)));
        cm.add(minerId, cfm.create(new InventoryComponent.Args(5, 1, List.of(new InventoryComponent.ReadableSlot(targetResource, 0)))));
        cm.add(minerId, cfm.create(new BuildingComponent.Args(width, height))); // Добавляем инфо о размере строения

        // Накалываем маркер тира для систем
        if (tier == 1) {
            cm.add(minerId, cfm.create(new TierOneMarkerComponent.Args()));
        } else if (tier == 2) {
            cm.add(minerId, cfm.create(new TierTwoMarkerComponent.Args()));
        }


        // Бронируем сетку на карте под эту сущность
        placementService.registerBuildingOnMap(minerId, x, y, width, height);

        activeEntities.add(minerEntity);
        return minerId;
    }

    private long createMiner(int x, int y, ResourceType resourceType, int tier) {
        long minerId = idGenerator.incrementAndGet();
        Entity minerEntity = new Entity(minerId);

        MiningComponent miningComponent = cfm.create(new MiningComponent.Args(resourceType, 1, 0));
        PositionComponent positionComponent = cfm.create(new PositionComponent.Args(x, y));
        InventoryComponent inventoryComponent = cfm.create(new InventoryComponent.Args(5, 1, List.of(new InventoryComponent.ReadableSlot(resourceType, 0))));


        cm.add(minerId, miningComponent);
        cm.add(minerId, positionComponent);
        cm.add(minerId, inventoryComponent);

        if (tier == 1) {
            cm.add(minerId, cfm.create(new TierOneMarkerComponent.Args()));
        } else if (tier == 2) {
            cm.add(minerId, cfm.create(new TierTwoMarkerComponent.Args()));
        }


        activeEntities.add(minerEntity);

        return minerId;
    }

    @Override
    public void run(String... args) throws Exception {
        /*System.out.println("Созданы два бура");
        long ironOre = createMiner(5, 20, ResourceType.IRON_ORE, Grade.TIER_1);
        System.out.println("Бур железной руды: " + ironOre);
        long copperOre = createMiner(5, 25, ResourceType.COPPER_ORE, Grade.TIER_2);
        System.out.println("Бур железной руды: " + copperOre);*/

        // Успешные постройки
        long miner1 = tryBuildMiner(5, 20, ResourceType.IRON_ORE, 1); // Ок, там железо
        long miner2 = tryBuildMiner(5, 25, ResourceType.COPPER_ORE, 2); // Ок, там медь

        // Ошибочные постройки (накажутся логикой)
        long badMiner1 = tryBuildMiner(5, 20, ResourceType.IRON_ORE, 1); // ❌ Ошибка: место уже занято miner1!
        long badMiner2 = tryBuildMiner(10, 10, ResourceType.IRON_ORE, 1); // ❌ Ошибка: на (10,10) обычная земля, а не жила железа!

    }


}
