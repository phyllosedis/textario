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
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierMarkers;
import ru.phyllosedis.textario.component.impl.meta.marker.transport.BeltMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.station.StationComponent;
import ru.phyllosedis.textario.component.impl.mining.MiningComponent;
import ru.phyllosedis.textario.component.impl.position.PositionComponent;
import ru.phyllosedis.textario.component.impl.transport.impl.InserterComponent;
import ru.phyllosedis.textario.type.ContentType;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class EntityBlueprintService {
    private final ComponentFactoryManager cfm;
    private final ComponentManager cm;
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
            case GAS ->
                    cm.add(id, cfm.create(new GasStateMarkerComponent.Args())); // Добавь GasStateMarkerComponent в проект
        }

        // Навешиваем маркер тира (TierOneMarkerComponent и т.д.) через наш автоматический хелпер
        cm.add(id, TierMarkers.get(tier));

        return id;
    }

    /**
     * 2. СОЗДАНИЕ КОНВЕЙЕРНЫХ ЛЕНТ
     */
    public long createBelt(int x, int y, int tier) {
        long id = prepareEntity(x, y, 1, 1);

        // Конвейер — это станция перемещения, ему нужен прогресс движения
        cm.add(id, cfm.create(new StationComponent.Args(1.0, 0.0)));
        cm.add(id, cfm.create(new ContentStateComponent.Args(ContentType.SOLID))); // Конвейеры возят только твердое

        // Маркеры для реактивного кэша логистики
        cm.add(id, cfm.create(new BeltMarkerComponent.Args()));
        cm.add(id, cfm.create(new SolidStateMarkerComponent.Args()));
        cm.add(id, TierMarkers.get(tier)); // Авто-тир скорости

        return id;
    }

    /**
     * 3. СОЗДАНИЕ МАНИПУЛЯТОРОВ / РОБО-РУК
     */
    public long createInserter(int x, int y, int tier) {
        long id = prepareEntity(x, y, 1, 1);

        // Манипулятор — это станция, рука крутится по таймеру
        cm.add(id, cfm.create(new StationComponent.Args(1.0, 0.0)));

        // Маркеры для реактивного кэша манипуляторов
        cm.add(id, cfm.create(new InserterComponent.Args()));
        cm.add(id, TierMarkers.get(tier)); // Настраивает скорость поворота руки из аннотации

        return id;
    }
}
