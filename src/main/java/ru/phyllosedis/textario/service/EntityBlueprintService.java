package ru.phyllosedis.textario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.Entity;
import ru.phyllosedis.textario.GameInitializer;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.inventory.InventoryComponent;
import ru.phyllosedis.textario.component.impl.meta.building.BuildingComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.ContentStateComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.state.gas.GasStateMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.state.liquid.LiquidStateMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.state.solid.SolidStateMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierComponent;
import ru.phyllosedis.textario.component.impl.meta.station.StationComponent;
import ru.phyllosedis.textario.component.impl.mining.MiningComponent;
import ru.phyllosedis.textario.component.impl.position.PositionComponent;
import ru.phyllosedis.textario.service.facroty.transport.BeltFactory;
import ru.phyllosedis.textario.service.facroty.transport.InserterFactory;
import ru.phyllosedis.textario.service.facroty.transport.SplitterFactory;
import ru.phyllosedis.textario.type.ContentType;
import ru.phyllosedis.textario.type.ResourceType;
import ru.phyllosedis.textario.type.Tier;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class EntityBlueprintService {
    private final ComponentFactoryManager cfm;
    private final ComponentManager cm;
    private final InserterFactory inserterFactory;
    private final SplitterFactory splitterFactory;
    private final BeltFactory beltFactory;
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
    public long createMiner(int x, int y, ResourceType resourceType, ContentType state, int tier) {
        long id = prepareEntity(x, y, 1, 1); // Буры обычно 1х1 клетки

        // Навешиваем производственную базу и тип копаемого ресурса
        cm.add(id, cfm.create(new StationComponent.Args(1.0, 0.0))); // Базовая скорость 1.0, прогресс 0
        cm.add(id, cfm.create(new MiningComponent.Args(resourceType)));

        // Паспорт консистенции для логистики
        cm.add(id, cfm.create(new ContentStateComponent.Args(state)));

        // Внутренний склад (инвентарь на 5 слотов со стаками по 1, как мы договаривались)
        cm.add(id, cfm.create(new InventoryComponent.Args(5, 1, List.of(new InventoryComponent.ReadableSlot(resourceType, 0)))));

        // Фильтры для реактивного кэша: навешиваем маркер состояния
        switch (state) {
            case SOLID -> cm.add(id, cfm.create(new SolidStateMarkerComponent.Args()));
            case LIQUID -> cm.add(id, cfm.create(new LiquidStateMarkerComponent.Args()));
            case GAS -> cm.add(id, cfm.create(new GasStateMarkerComponent.Args()));
        }

        // Навешиваем маркер тира (TierOneMarkerComponent и т.д.) через наш автоматический хелпер
//        cm.add(id, TierMarkers.get(tier));
        cm.add(id, cfm.create(new TierComponent.Args(Tier.UNDEFINED.getByOrdinal(tier))));

        return id;
    }

    /**
     * 2. СОЗДАНИЕ КОНВЕЙЕРНЫХ ЛЕНТ
     */
    public long createBelt(int x, int y, int tier) {
        long id = prepareEntity(x, y, 1, 1);

        beltFactory.create(id, tier);
        return id;
    }

    /**
     * 3. СОЗДАНИЕ МАНИПУЛЯТОРОВ / РОБО-РУК
     */
    public long createInserter(int x, int y, int tier) {
        long id = prepareEntity(x, y, 1, 1);
        inserterFactory.create(id, tier);
        return id;
    }

    public long createSplitter(int x, int y, int tier, int splitMode) {
        long id = prepareEntity(x, y, 1, 1);
        splitterFactory.create(id, tier, splitMode);
        return id;
    }
}
