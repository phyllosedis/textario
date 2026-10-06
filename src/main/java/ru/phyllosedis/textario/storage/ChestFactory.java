package ru.phyllosedis.textario.storage;

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

import java.util.List;

@Component
@AssociatedMarker(ChestMarkerComponent.class)
public class ChestFactory extends AbstractEntityFactory<ChestFactory.Args> {

    public ChestFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(Args args) {
        super.create(args);

        long id = args.getId();
        ChestBalance.ChestStats stats = bf.getStats(ChestBalance.class, args.getTier());

        cm.add(id, cfm.create(new ChestMarkerComponent.Args()));
        cm.add(id, cfm.create(new ContentStateComponent.Args(ContentState.SOLID)));
        cm.add(id, cfm.create(new InventoryComponent.Args(stats.getSize(), stats.getStackSize(), List.of())));
    }

    @Getter
    @SuperBuilder
    public static class Args extends AbstractEntityFactory.Args {
    }
}
