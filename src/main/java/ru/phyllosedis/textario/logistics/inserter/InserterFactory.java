package ru.phyllosedis.textario.logistics.inserter;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.production.station.StationMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.resource.Tier;

import java.util.List;

@Component
@AssociatedMarker(InserterMarkerComponent.class)
public class InserterFactory extends AbstractEntityFactory {

    public InserterFactory(ComponentManager cm, ComponentFactoryManager cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = Tier.UNDEFINED.getByOrdinal(tierInt);

        InserterBalance.InserterStats stats = bf.getStats(InserterBalance.class, tier);
        cm.add(id, cfm.create(new StationMarkerComponent.Args()));
        cm.add(id, cfm.create(new TierMarkerComponent.Args()));
        cm.add(id, cfm.create(new InserterComponent.Args(stats.getTransferSpeed(), stats.getRange(), stats.getStackSize())));
        cm.add(id, cfm.create(new InventoryComponent.Args(1, stats.getStackSize(), List.of())));
    }

}
