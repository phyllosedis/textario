package ru.phyllosedis.textario.engine.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.logistics.belt.BeltFactory;
import ru.phyllosedis.textario.logistics.inserter.InserterFactory;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.logistics.splitter.SplitterFactory;
import ru.phyllosedis.textario.production.furnace.FurnaceFactory;
import ru.phyllosedis.textario.production.mining.MinerFactory;
import ru.phyllosedis.textario.resource.ResourceCategory;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;
import ru.phyllosedis.textario.storage.ChestFactory;
import ru.phyllosedis.textario.world.PlacementService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class EntityBlueprintService {
    private final PlacementService ps;
    private final EntityFactoryRegistry ef;

    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Long, String> createdEntities = new ConcurrentHashMap<>();

    public Map<Long, String> createdEntities() {
        return Map.copyOf(createdEntities);
    }

    private void note(long id, String description) {
        createdEntities.put(id, description);
    }

    /**
     * Базовый хелпер для создания сущности и её первичной обвязки (позиция, геометрия)
     */
    private long prepareEntity(int x, int y, int width, int height, ResourceCategory requiredCategory) {
        long id = idGenerator.incrementAndGet();

        if (!ps.canPlace(x, y, width, height, requiredCategory)) {
            throw new IllegalArgumentException("нельзя построиться на " + x + ":" + y + " " + width + "x" + height);
        }
        ps.registerBuildingOnMap(id, x, y, width, height);


        return id;
    }

    /**
     * 1. СОЗДАНИЕ БУРОВ / ШАХТ (Разные состояния материи)
     */
    public long createMiner(int x, int y, Tier tier, ResourceType resourceType) {
        long id = prepareEntity(x, y, 2, 2, ResourceCategory.ORE);

        ef.get(MinerFactory.class)
                .create(MinerFactory.Args.builder()
                        .id(id)
                        .x(x)
                        .y(y)
                        .width(2)
                        .height(2)
                        .tier(tier)
                        .contentState(resourceType.getState())
                        .resourceCategory(ResourceCategory.ORE)
                        .resourceType(resourceType)
                        .build());
        note(id, "miner@" + x + ":" + y + " " + resourceType);
        return id;
    }

    /**
     * 2. СОЗДАНИЕ КОНВЕЙЕРНЫХ ЛЕНТ
     */
    public long createBelt(int x, int y, Tier tier, ResourceType resourceType) {
        long id = prepareEntity(x, y, 1, 1, ResourceCategory.SOIL);
        BeltFactory beltFactory = ef.get(BeltFactory.class);
        beltFactory.create(BeltFactory.Args.builder()
                .id(id)
                .x(x)
                .y(y)
                .width(1)
                .height(1)
                .tier(tier)
                .build());
        note(id, "belt@" + x + ":" + y);
        return id;
    }

    /**
     * 3. СОЗДАНИЕ МАНИПУЛЯТОРОВ / РОБО-РУК
     */
    public long createInserter(int x, int y, Tier tier, ResourceType resourceType) {
        long id = prepareEntity(x, y, 1, 1, ResourceCategory.SOIL);
        ef.get(InserterFactory.class).create(InserterFactory.Args.builder()
                .id(id)
                .tier(tier)
                .x(x)
                .y(y)
                .width(1)
                .height(1)
                .build());
        note(id, "inserter@" + x + ":" + y);
        return id;
    }

    public long createSplitter(int x, int y, Tier tier, ResourceType resourceType, SplitMode splitMode) {
        long id = prepareEntity(x, y, 2, 1, ResourceCategory.SOIL);
        ef.get(SplitterFactory.class).create(SplitterFactory.Args.builder()
                .splitMode(splitMode)
                .id(id)
                .tier(tier)
                .x(x)
                .y(y)
                .width(2)
                .height(1)
                .build());
        note(id, "splitter@" + x + ":" + y + " " + splitMode);
        return id;
    }

    public long createChest(int x, int y, Tier tier) {
        long id = prepareEntity(x, y, 1, 1, ResourceCategory.SOIL);
        ef.get(ChestFactory.class).create(ChestFactory.Args.builder()
                .id(id)
                .tier(tier)
                .x(x)
                .y(y)
                .width(1)
                .height(1)
                .build());
        note(id, "chest@" + x + ":" + y);
        return id;
    }

    public long createFurnace(int x, int y, Tier tier) {
        long id = prepareEntity(x, y, 2, 2, ResourceCategory.SOIL);
        ef.get(FurnaceFactory.class).create(FurnaceFactory.Args.builder()
                .id(id)
                .tier(tier)
                .x(x)
                .y(y)
                .width(2)
                .height(2)
                .build());
        note(id, "furnace@" + x + ":" + y);
        return id;
    }
}
