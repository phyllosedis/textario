package ru.phyllosedis.textario.logistics.splitter;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.engine.ecs.entity.AbstractEntityFactory;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.logistics.ContentStateComponent;
import ru.phyllosedis.textario.logistics.port.LogisticPort;
import ru.phyllosedis.textario.logistics.port.PortSide;
import ru.phyllosedis.textario.logistics.port.PortType;
import ru.phyllosedis.textario.resource.ContentState;

import ru.phyllosedis.textario.resource.ContentState;
import ru.phyllosedis.textario.world.RotationComponent;

import java.util.List;

@Component
@AssociatedMarker(SplitterMarkerComponent.class)
public class SplitterFactory extends AbstractEntityFactory<SplitterFactory.Args> {

    public SplitterFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }


    @Override
    public void create(Args args) {
        super.create(args);
        long id = args.getId();

        cm.add(id, cfm.create(new SplitterMarkerComponent.Args()));
        cm.add(id, cfm.create(new SplitterComponent.Args(args.getSplitMode())));
        cm.add(id, cfm.create(new ContentStateComponent.Args(ContentState.SOLID)));
        cm.add(id, cfm.create(new InventoryComponent.Args(2, 4, List.of())));
        cm.add(id, cfm.create(new RotationComponent.Args(args.getRotation())));
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
                ),
                new LogisticPort.ReadablePort(
                        2,
                        PortType.OUTPUT,
                        PortSide.RIGHT
                )
        ))));
    }

    @Getter
    @SuperBuilder
    public static class Args extends AbstractEntityFactory.Args {
        private final SplitMode splitMode;
        private final int rotation;
    }
}
