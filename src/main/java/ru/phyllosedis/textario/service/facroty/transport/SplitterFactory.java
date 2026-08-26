package ru.phyllosedis.textario.service.facroty.transport;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.SplitterBalance;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.BeltComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.SplitterComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.TransportPortComponent;
import ru.phyllosedis.textario.service.facroty.AbstractTransportFactory;
import ru.phyllosedis.textario.type.PortSide;
import ru.phyllosedis.textario.type.PortType;
import ru.phyllosedis.textario.type.SplitMode;
import ru.phyllosedis.textario.type.Tier;

import java.util.List;

@Component
public class SplitterFactory extends AbstractTransportFactory<SplitterComponent> {

    public SplitterFactory(ComponentManager cm, ComponentFactoryManager cfm) {
        super(cm, cfm, SplitterComponent.class);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = getTier(tierInt);
        create(id, tier, SplitMode.ROUND_ROBIN);
    }

    public void create(long id, int tierInt, int splitMode) {
        Tier tier = getTier(tierInt);
        create(id, tier, getSplitMode(splitMode));
    }

    public void create(long id, Tier tier, SplitMode mode) {
        SplitterBalance.SplitterStats stats = SplitterBalance.stats(tier);

        cm.add(id, cfm.create(new BeltComponent.Args(stats.speed(), stats.throughput())));
        cm.add(id, cfm.create(new SplitterComponent.Args(mode)));
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

    public SplitMode getSplitMode(int splitModeInt) {
        SplitMode splitMode;
        try {
            splitMode = SplitMode.UNDEFINED.getByOrdinal(splitModeInt);
        } catch (Exception e) {
            throw new IllegalArgumentException("Невозможно создать " + type.getName() + " уровня " + splitModeInt);
        }
        return splitMode;
    }
}
