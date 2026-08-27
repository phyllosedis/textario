package ru.phyllosedis.textario.service.factory.miner;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.BalanceFactory;
import ru.phyllosedis.textario.balance.miner.MinerBalance;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.mining.MiningMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.station.StationComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierComponent;
import ru.phyllosedis.textario.component.impl.mining.ExtractionComponent;
import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;
import ru.phyllosedis.textario.service.factory.marker.AssociatedMarker;
import ru.phyllosedis.textario.type.Tier;

@Component
@AssociatedMarker(MiningMarkerComponent.class)
public class MinerFactory extends AbstractEntityFactory {

    public MinerFactory(ComponentManager cm, ComponentFactoryManager cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = getTier(tierInt);
        MinerBalance.MinerStats stats = bf.getStats(MinerBalance.class, tier);

        cm.add(id, cfm.create(new StationComponent.Args()));
        cm.add(id, cfm.create(new TierComponent.Args()));
        cm.add(id, cfm.create(new ExtractionComponent.Args(stats.getSpeed())));
    }

}
