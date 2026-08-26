package ru.phyllosedis.textario.service.facroty.transport;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.InserterBalance;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.InserterComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.tier.TierComponent;
import ru.phyllosedis.textario.component.impl.meta.station.StationComponent;
import ru.phyllosedis.textario.service.facroty.AbstractTransportFactory;
import ru.phyllosedis.textario.type.Tier;

@Component
public class InserterFactory extends AbstractTransportFactory<InserterComponent> {
    public InserterFactory(ComponentManager cm, ComponentFactoryManager cfm) {
        super(cm, cfm, InserterComponent.class);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = getTier(tierInt);

        InserterBalance.InserterStats stats = InserterBalance.stats(tier);

        cm.add(id, cfm.create(new TierComponent.Args(tier)));
        cm.add(id, cfm.create(new InserterComponent.Args(stats.transferSpeed(), stats.range(), stats.stackSize())));
        cm.add(id, cfm.create(new StationComponent.Args(stats.transferSpeed(), 0.0)));
    }

}
