package ru.phyllosedis.textario.service.factory.transport.conveyor.splitter;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.BalanceFactory;
import ru.phyllosedis.textario.balance.transport.conveyor.splitter.SplitterBalance;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.BeltComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.SplitterComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl.TransportPortComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.transport.SplitterMarkerComponent;
import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;
import ru.phyllosedis.textario.service.factory.marker.AssociatedMarker;
import ru.phyllosedis.textario.type.PortSide;
import ru.phyllosedis.textario.type.PortType;
import ru.phyllosedis.textario.type.SplitMode;
import ru.phyllosedis.textario.type.Tier;

import java.util.List;

@Component
@AssociatedMarker(SplitterMarkerComponent.class)
public class SplitterFactory extends AbstractEntityFactory {

    public SplitterFactory(ComponentManager cm, ComponentFactoryManager cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
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
        SplitterBalance.SplitterStats stats = bf.getStats(SplitterBalance.class, tier);

        cm.add(id, cfm.create(new BeltComponent.Args(stats.getSpeed(), stats.getThroughput())));
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
            throw new IllegalArgumentException("Невозможно создать " + this.getClass().getName() + " уровня " + splitModeInt);
        }
        return splitMode;
    }
}
