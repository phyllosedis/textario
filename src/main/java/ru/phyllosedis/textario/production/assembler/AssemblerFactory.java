package ru.phyllosedis.textario.production.assembler;

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
import ru.phyllosedis.textario.production.ProduceSpeedComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.production.station.StationMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.resource.ContentState;

import java.util.List;

@Component
@AssociatedMarker(AssemblerMarkerComponent.class)
public class AssemblerFactory extends AbstractEntityFactory<AssemblerFactory.Args> {

    public AssemblerFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(Args args) {
        super.create(args);

        long id = args.getId();
        AssemblerBalance.AssemblerStats stats = bf.getStats(AssemblerBalance.class, args.getTier());

        cm.add(id, cfm.create(new StationMarkerComponent.Args()));
        cm.add(id, cfm.create(new ProgressComponent.Args(0)));
        cm.add(id, cfm.create(new ContentStateComponent.Args(ContentState.SOLID)));
        cm.add(id, cfm.create(new TierMarkerComponent.Args()));
        cm.add(id, cfm.create(new ProduceSpeedComponent.Args(stats.getSpeed())));
        cm.add(id, cfm.create(new AssemblerMarkerComponent.Args()));
        cm.add(id, cfm.create(new AssemblerComponent.Args("")));
        cm.add(id, cfm.create(new InventoryComponent.Args(4, 50, List.of())));
    }

    @Getter
    @SuperBuilder
    public static class Args extends AbstractEntityFactory.Args {
    }
}
