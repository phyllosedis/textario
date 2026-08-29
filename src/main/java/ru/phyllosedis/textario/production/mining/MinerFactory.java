package ru.phyllosedis.textario.production.mining;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.entity.AbstractEntityFactory;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.logistics.ContentStateComponent;
import ru.phyllosedis.textario.production.ProduceSpeedComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.production.station.StationMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.resource.ContentState;
import ru.phyllosedis.textario.resource.ResourceCategory;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.marker.GasStateMarkerComponent;
import ru.phyllosedis.textario.resource.marker.LiquidStateMarkerComponent;
import ru.phyllosedis.textario.resource.marker.SolidStateMarkerComponent;

import java.util.List;
import java.util.Objects;

@Component
@AssociatedMarker(MiningMarkerComponent.class)
public class MinerFactory extends AbstractEntityFactory<MinerFactory.Args> {

    public MinerFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(Args args) {
        super.create(args);

        long id = args.getId();
        MinerBalance.MinerStats stats = bf.getStats(MinerBalance.class, args.getTier());

        cm.add(id, cfm.create(new StationMarkerComponent.Args()));
        cm.add(id, cfm.create(new ProgressComponent.Args(0)));
        cm.add(id, cfm.create(new ContentStateComponent.Args(args.contentState)));
        cm.add(id, cfm.create(new TierMarkerComponent.Args()));
        cm.add(id, cfm.create(new ProduceSpeedComponent.Args(stats.getSpeed())));
        cm.add(id, cfm.create(new MiningComponent.Args(args.resourceType)));
        cm.add(id, cfm.create(new MiningMarkerComponent.Args()));
        cm.add(id, cfm.create(new InventoryComponent.Args(1, 99, List.of())));
        cm.add(id, cfm.create(Objects.requireNonNull(getStateMarker(args.getContentState()))));
    }

    @SuperBuilder
    @Getter
    public static class Args extends AbstractEntityFactory.Args {
        private final ContentState contentState;
        private final ResourceType resourceType;
        private final ResourceCategory resourceCategory;
    }

    private ComponentArgs<? extends ru.phyllosedis.textario.engine.ecs.component.Component> getStateMarker(ContentState state) {
        return switch (state) {
            case SOLID -> new SolidStateMarkerComponent.Args();
            case LIQUID -> new LiquidStateMarkerComponent.Args();
            case GAS -> new GasStateMarkerComponent.Args();
            case UNDEFINED -> null;
        };
    }

}
