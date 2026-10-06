package ru.phyllosedis.textario.combat;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.engine.ecs.entity.AbstractEntityFactory;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.List;

@Component
@AssociatedMarker(TurretMarkerComponent.class)
public class TurretFactory extends AbstractEntityFactory<TurretFactory.Args> {

    public TurretFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(Args args) {
        super.create(args);

        long id = args.getId();
        TurretBalance.TurretStats stats = bf.getStats(TurretBalance.class, args.getTier());

        cm.add(id, cfm.create(new TurretMarkerComponent.Args()));
        cm.add(id, cfm.create(new TurretComponent.Args(
                stats.getRange(), stats.getDamage(), stats.getInterval(), ResourceType.COPPER_AMMO)));
        cm.add(id, cfm.create(new InventoryComponent.Args(2, 50, List.of())));
        cm.add(id, cfm.create(new HealthComponent.Args(stats.getHp(), stats.getHp())));
        cm.add(id, cfm.create(new ProgressComponent.Args(0)));
    }

    @Getter
    @SuperBuilder
    public static class Args extends AbstractEntityFactory.Args {
    }
}
