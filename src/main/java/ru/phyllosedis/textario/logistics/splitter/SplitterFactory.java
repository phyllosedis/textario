package ru.phyllosedis.textario.logistics.splitter;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.logistics.belt.BeltComponent;
import ru.phyllosedis.textario.logistics.port.LogisticPort;
import ru.phyllosedis.textario.service.factory.AbstractEntityFactory;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.logistics.port.PortSide;
import ru.phyllosedis.textario.logistics.port.PortType;
import ru.phyllosedis.textario.resource.Tier;

import java.util.List;

@Component
@AssociatedMarker(SplitterMarkerComponent.class)
public class SplitterFactory extends AbstractEntityFactory {

    public SplitterFactory(ComponentManager cm, ComponentFactoryManager cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(long id, int tierInt) {
        Tier tier = Tier.UNDEFINED.getByOrdinal(tierInt);
        create(id, tier, SplitMode.ROUND_ROBIN);
    }

    public void create(long id, int tierInt, int splitMode) {
        Tier tier = Tier.UNDEFINED.getByOrdinal(tierInt);
        create(id, tier, getSplitMode(splitMode));
    }

    public void create(long id, Tier tier, SplitMode mode) {
        SplitterBalance.SplitterStats stats = bf.getStats(SplitterBalance.class, tier);

        cm.add(id, cfm.create(new BeltComponent.Args(stats.getSpeed(), stats.getThroughput())));
        cm.add(id, cfm.create(new SplitterComponent.Args(mode)));
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
