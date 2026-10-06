package ru.phyllosedis.textario.logistics.underground;

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
import ru.phyllosedis.textario.resource.ContentState;
import ru.phyllosedis.textario.world.RotationComponent;

import java.util.List;

@Component
@AssociatedMarker(UndergroundMarkerComponent.class)
public class UndergroundFactory extends AbstractEntityFactory<UndergroundFactory.Args> {

    public UndergroundFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(Args args) {
        super.create(args);

        long id = args.getId();

        cm.add(id, cfm.create(new UndergroundMarkerComponent.Args()));
        cm.add(id, cfm.create(new UndergroundComponent.Args(args.getMode())));
        cm.add(id, cfm.create(new ContentStateComponent.Args(ContentState.SOLID)));
        cm.add(id, cfm.create(new InventoryComponent.Args(1, 4, List.of())));
        cm.add(id, cfm.create(new RotationComponent.Args(args.getRotation())));
    }

    @Getter
    @SuperBuilder
    public static class Args extends AbstractEntityFactory.Args {
        private final UndergroundMode mode;
        private final int rotation;
    }
}
