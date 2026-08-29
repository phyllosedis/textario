package ru.phyllosedis.textario.engine.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.logistics.belt.BeltFactory;
import ru.phyllosedis.textario.logistics.inserter.InserterFactory;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.logistics.splitter.SplitterFactory;
import ru.phyllosedis.textario.production.mining.MinerFactory;
import ru.phyllosedis.textario.resource.ResourceCategory;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;
import ru.phyllosedis.textario.world.PlacementService;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class EntityBlueprintService {
    private final PlacementService ps;
    private final EntityFactoryRegistry ef;

    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Базовый хелпер для создания сущности и её первичной обвязки (позиция, геометрия)
     */
    private long prepareEntity(int x, int y, int width, int height, ResourceCategory requiredCategory, ResourceType resourceType) {
        long id = idGenerator.incrementAndGet();

        if (!ps.canPlace(x, y, width, height, requiredCategory, resourceType)) {
            throw new IllegalArgumentException("нельзя построиться на " + resourceType.name() + " " + resourceType.getCategory().name() + " " + resourceType.getState());
        }
        ps.registerBuildingOnMap(id, x, y, width, height);


        return id;
    }

    /**
     * 1. СОЗДАНИЕ БУРОВ / ШАХТ (Разные состояния материи)
     */
    public long createMiner(int x, int y, Tier tier, ResourceType resourceType) {
        long id = prepareEntity(x, y, 1, 1, ResourceCategory.ORE, resourceType);

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
        return id;
    }

    /**
     * 2. СОЗДАНИЕ КОНВЕЙЕРНЫХ ЛЕНТ
     */
    public long createBelt(int x, int y, Tier tier, ResourceType resourceType) {
        long id = prepareEntity(x, y, 1, 1, resourceType.getCategory(), resourceType);
        BeltFactory beltFactory = ef.get(BeltFactory.class);
        beltFactory.create(BeltFactory.Args.builder()
                .id(id)
                .x(x)
                .y(y)
                .width(1)
                .height(1)
                .tier(tier)
                .build());
        return id;
    }

    /**
     * 3. СОЗДАНИЕ МАНИПУЛЯТОРОВ / РОБО-РУК
     */
    public long createInserter(int x, int y, Tier tier, ResourceType resourceType) {
        long id = prepareEntity(x, y, 1, 1, resourceType.getCategory(), resourceType);
        ef.get(InserterFactory.class).create(InserterFactory.Args.builder()
                .id(id)
                .tier(tier)
                .x(x)
                .y(y)
                .width(1)
                .height(1)
                .build());
        return id;
    }

    public long createSplitter(int x, int y, Tier tier, ResourceType resourceType, SplitMode splitMode) {
        long id = prepareEntity(x, y, 1, 1, resourceType.getCategory(), resourceType);
        ef.get(SplitterFactory.class).create(SplitterFactory.Args.builder()
                .splitMode(splitMode)
                .id(id)
                .tier(tier)
                .x(x)
                .y(y)
                .width(2)
                .height(1)
                .build());
        return id;
    }
}
