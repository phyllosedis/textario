package ru.phyllosedis.textario.service.factory.transport.inserter;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.BalanceFactory;
import ru.phyllosedis.textario.balance.transport.inserter.InserterBalance;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.inventory.InventoryComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.InserterComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.station.StationComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.transport.InserterMarkerComponent;
import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;
import ru.phyllosedis.textario.service.factory.marker.AssociatedMarker;
import ru.phyllosedis.textario.type.Tier;

import java.util.List;

@Component
@AssociatedMarker(InserterMarkerComponent.class)
public class InserterFactory extends AbstractEntityFactory {

    public InserterFactory(ComponentManager cm, ComponentFactoryManager cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = getTier(tierInt);

        InserterBalance.InserterStats stats = bf.getStats(InserterBalance.class, tier);
        cm.add(id, cfm.create(new StationComponent.Args()));
        cm.add(id, cfm.create(new TierComponent.Args()));
        cm.add(id, cfm.create(new InserterComponent.Args(stats.getTransferSpeed(), stats.getRange(), stats.getStackSize())));
        cm.add(id, cfm.create(new InventoryComponent.Args(1, stats.getStackSize(), List.of())));
    }

}
