package ru.phyllosedis.textario.logistics.inserter;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.engine.ecs.entity.AbstractEntityFactory;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.logistics.port.LogisticPort;
import ru.phyllosedis.textario.logistics.port.PortSide;
import ru.phyllosedis.textario.logistics.port.PortType;
import ru.phyllosedis.textario.production.ProduceSpeedComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.production.station.StationMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.resource.ContentState;

import java.util.List;
import java.util.Set;

@Component
@AssociatedMarker(InserterMarkerComponent.class)
public class InserterFactory extends AbstractEntityFactory<InserterFactory.Args> {

    public InserterFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(Args args) {
        super.create(args);

        long id = args.getId();

        InserterBalance.InserterStats stats = bf.getStats(InserterBalance.class, args.getTier());
        cm.add(id, cfm.create(new StationMarkerComponent.Args()));
        cm.add(id, cfm.create(new TierMarkerComponent.Args()));
        cm.add(id, cfm.create(new InserterComponent.Args(stats.getTransferSpeed(), stats.getRange(), stats.getStackSize(), Set.of(ContentState.SOLID))));
        cm.add(id, cfm.create(new InventoryComponent.Args(1, stats.getStackSize(), List.of())));
        cm.add(id, cfm.create(new ProduceSpeedComponent.Args(stats.getTransferSpeed())));
        cm.add(id, cfm.create(new ProgressComponent.Args(0)));
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

    @Getter
    @SuperBuilder
    public static class Args extends AbstractEntityFactory.Args {
    }

}
