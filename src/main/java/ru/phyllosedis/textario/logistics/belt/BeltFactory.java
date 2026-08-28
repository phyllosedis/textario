package ru.phyllosedis.textario.logistics.belt;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.logistics.ContentStateComponent;
import ru.phyllosedis.textario.production.mining.solid.SolidStateMarkerComponent;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.logistics.port.PortSide;
import ru.phyllosedis.textario.logistics.port.PortType;
import ru.phyllosedis.textario.logistics.port.LogisticPort;
import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;
import ru.phyllosedis.textario.resource.ContentState;
import ru.phyllosedis.textario.resource.Tier;

import java.util.List;

@Component
@AssociatedMarker(BeltMarkerComponent.class)
public class BeltFactory extends AbstractEntityFactory {

    public BeltFactory(ComponentManager cm, ComponentFactoryManager cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = Tier.UNDEFINED.getByOrdinal(tierInt);

        BeltBalance.BeltStats stats = bf.getStats(BeltBalance.class, tier);

        cm.add(id, cfm.create(new BeltMarkerComponent.Args()));
        cm.add(id, cfm.create(new SolidStateMarkerComponent.Args()));
        cm.add(id, cfm.create(new BeltComponent.Args(stats.getSpeed(), stats.getThroughput())));
        cm.add(id, cfm.create(new ContentStateComponent.Args(ContentState.SOLID)));
        cm.add(id, cfm.create(new LogisticPort.Args(List.of(
                new LogisticPort.ReadablePort(
                        0,
                        PortType.INPUT,
                        PortSide.BACK
                ),
                new LogisticPort.ReadablePort(
                        1,
                        PortType.OUTPUT,
                        PortSide.FRONT
                )
        ))));
    }
}
