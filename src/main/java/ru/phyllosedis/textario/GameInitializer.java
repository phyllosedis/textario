package ru.phyllosedis.textario;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.component.*;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.inventory.InventoryComponent;
import ru.phyllosedis.textario.component.impl.mining.MiningComponent;
import ru.phyllosedis.textario.component.impl.position.PositionComponent;
import ru.phyllosedis.textario.type.Grade;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class GameInitializer implements CommandLineRunner {

    private final ComponentFactoryManager cfm;
    private final ComponentManager cm;
    public static final List<Entity> activeEntities = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    private long createMiner(int x, int y, ResourceType resourceType, Grade grade){
        long minerId = idGenerator.incrementAndGet();
        Entity minerEntity = new Entity(minerId);

        MiningComponent miningComponent = cfm.create(new MiningComponent.Args(resourceType, 1, 0, grade));
        PositionComponent positionComponent = cfm.create(new PositionComponent.Args(x, y));
        InventoryComponent inventoryComponent = cfm.create(new InventoryComponent.Args(5, List.of(new InventoryComponent.ReadableSlot(resourceType, 0))));


        cm.add(minerId, miningComponent);
        cm.add(minerId, positionComponent);
        cm.add(minerId, inventoryComponent);

        activeEntities.add(minerEntity);

        return minerId;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Созданы два бура");
        long ironOre = createMiner(5, 20, ResourceType.IRON_ORE, Grade.TIER_1);
        System.out.println("Бур железной руды: " + ironOre);
        long copperOre = createMiner(5, 25, ResourceType.COPPER_ORE, Grade.TIER_2);
        System.out.println("Бур железной руды: " + copperOre);


    }


}
