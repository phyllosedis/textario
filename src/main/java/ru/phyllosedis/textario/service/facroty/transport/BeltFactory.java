package ru.phyllosedis.textario.service.facroty.transport;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.BeltBalance;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.logistic.ContentStateComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.BeltComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.TransportPortComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.state.solid.SolidStateMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.transport.BeltMarkerComponent;
import ru.phyllosedis.textario.service.facroty.AbstractTransportFactory;
import ru.phyllosedis.textario.type.ContentType;
import ru.phyllosedis.textario.type.PortSide;
import ru.phyllosedis.textario.type.PortType;
import ru.phyllosedis.textario.type.Tier;

import java.util.List;

@Component
public class BeltFactory extends AbstractTransportFactory<BeltComponent> {
    public BeltFactory(ComponentManager cm, ComponentFactoryManager cfm) {
        super(cm, cfm, BeltComponent.class);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = getTier(tierInt);

        BeltBalance.BeltStats stats = BeltBalance.stats(tier);

        cm.add(id, cfm.create(new BeltMarkerComponent.Args()));
        cm.add(id, cfm.create(new SolidStateMarkerComponent.Args()));
        cm.add(id, cfm.create(new BeltComponent.Args(stats.speed(), stats.throughput())));
        cm.add(id, cfm.create(new ContentStateComponent.Args(ContentType.SOLID)));
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
