package ru.phyllosedis.textario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.ecs.Entity;
import ru.phyllosedis.textario.bootstrap.GameInitializer;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.logistics.ContentStateComponent;
import ru.phyllosedis.textario.production.mining.gas.GasStateMarkerComponent;
import ru.phyllosedis.textario.production.mining.liquid.LiquidStateMarkerComponent;
import ru.phyllosedis.textario.production.mining.solid.SolidStateMarkerComponent;
import ru.phyllosedis.textario.production.mining.MiningComponent;
import ru.phyllosedis.textario.world.PositionComponent;
import ru.phyllosedis.textario.production.mining.MinerFactory;
import ru.phyllosedis.textario.logistics.belt.BeltFactory;
import ru.phyllosedis.textario.logistics.splitter.SplitterFactory;
import ru.phyllosedis.textario.logistics.inserter.InserterFactory;
import ru.phyllosedis.textario.resource.ContentState;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class EntityBlueprintService {
    private final ComponentFactoryManager cfm;
    private final ComponentManager cm;

    private final EntityFactory ef;

    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Базовый хелпер для создания сущности и её первичной обвязки (позиция, геометрия)
     */
    private long prepareEntity(int x, int y, int width, int height) {
        long id = idGenerator.incrementAndGet();
        Entity entity = new Entity(id);

        cm.add(id, cfm.create(new PositionComponent.Args(x, y)));
        cm.add(id, cfm.create(new BuildingComponent.Args(width, height)));

        GameInitializer.activeEntities.add(entity); // Регистрируем в глобальном списке
        return id;
    }

    /**
     * 1. СОЗДАНИЕ БУРОВ / ШАХТ (Разные состояния материи)
     */
    public long createMiner(int x, int y, ResourceType resourceType, ContentState state, int tier) {
        long id = prepareEntity(x, y, 1, 1);

        ef.get(MinerFactory.class).create(id, tier);

        cm.add(id, cfm.create(new MiningComponent.Args(resourceType)));
        cm.add(id, cfm.create(new ContentStateComponent.Args(state)));

        switch (state) {
            case SOLID -> cm.add(id, cfm.create(new SolidStateMarkerComponent.Args()));
            case LIQUID -> cm.add(id, cfm.create(new LiquidStateMarkerComponent.Args()));
            case GAS -> cm.add(id, cfm.create(new GasStateMarkerComponent.Args()));
        }
        return id;
    }

    /**
     * 2. СОЗДАНИЕ КОНВЕЙЕРНЫХ ЛЕНТ
     */
    public long createBelt(int x, int y, int tier) {
        long id = prepareEntity(x, y, 1, 1);
        ef.get(BeltFactory.class).create(id, tier);
        return id;
    }

    /**
     * 3. СОЗДАНИЕ МАНИПУЛЯТОРОВ / РОБО-РУК
     */
    public long createInserter(int x, int y, int tier) {
        long id = prepareEntity(x, y, 1, 1);
        ef.get(InserterFactory.class).create(id, tier);
        return id;
    }

    public long createSplitter(int x, int y, int tier, int splitMode) {
        long id = prepareEntity(x, y, 1, 1);
        ef.get(SplitterFactory.class).create(id, tier, splitMode);
        return id;
    }
}
