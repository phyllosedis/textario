package ru.phyllosedis.textario.production.mining;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.production.station.StationMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.resource.Tier;

@Component
@AssociatedMarker(MiningMarkerComponent.class)
public class MinerFactory extends AbstractEntityFactory {

    public MinerFactory(ComponentManager cm, ComponentFactoryManager cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = Tier.UNDEFINED.getByOrdinal(tierInt);
        MinerBalance.MinerStats stats = bf.getStats(MinerBalance.class, tier);

        cm.add(id, cfm.create(new StationMarkerComponent.Args()));
        cm.add(id, cfm.create(new TierMarkerComponent.Args()));
        cm.add(id, cfm.create(new ExtractionComponent.Args(stats.getSpeed())));
    }

}
