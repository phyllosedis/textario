package ru.phyllosedis.textario.service.factory.transport.conveyor.belt;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.BalanceFactory;
import ru.phyllosedis.textario.balance.transport.conveyor.belt.BeltBalance;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.logistic.ContentStateComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.BeltComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.TransportPortComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.state.solid.SolidStateMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.transport.BeltMarkerComponent;
import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;
import ru.phyllosedis.textario.service.factory.marker.AssociatedMarker;
import ru.phyllosedis.textario.type.ContentState;
import ru.phyllosedis.textario.type.PortSide;
import ru.phyllosedis.textario.type.PortType;
import ru.phyllosedis.textario.type.Tier;

import java.util.List;

@Component
@AssociatedMarker(BeltMarkerComponent.class)
public class BeltFactory extends AbstractEntityFactory {

    public BeltFactory(ComponentManager cm, ComponentFactoryManager cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = getTier(tierInt);

        BeltBalance.BeltStats stats = bf.getStats(BeltBalance.class, tier);

        cm.add(id, cfm.create(new BeltMarkerComponent.Args()));
        cm.add(id, cfm.create(new SolidStateMarkerComponent.Args()));
        cm.add(id, cfm.create(new BeltComponent.Args(stats.getSpeed(), stats.getThroughput())));
        cm.add(id, cfm.create(new ContentStateComponent.Args(ContentState.SOLID)));
        cm.add(id, cfm.create(new TransportPortComponent.Args(List.of(
                new TransportPortComponent.ReadablePort(
                        0,
                        PortType.INPUT,
                        PortSide.BACK
                ),
                new TransportPortComponent.ReadablePort(
                        1,
                        PortType.OUTPUT,
                        PortSide.FRONT
                )
        ))));
    }
}
