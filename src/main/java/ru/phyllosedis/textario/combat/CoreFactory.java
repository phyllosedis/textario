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

import java.util.List;

@Component
@AssociatedMarker(CoreMarkerComponent.class)
public class CoreFactory extends AbstractEntityFactory<CoreFactory.Args> {

    public CoreFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(Args args) {
        super.create(args);

        long id = args.getId();
        CoreBalance.CoreStats stats = bf.getStats(CoreBalance.class, args.getTier());

        cm.add(id, cfm.create(new CoreMarkerComponent.Args()));
        cm.add(id, cfm.create(new HealthComponent.Args(stats.getHp(), stats.getHp())));
        cm.add(id, cfm.create(new InventoryComponent.Args(8, 200, List.of())));
    }

    @Getter
    @SuperBuilder
    public static class Args extends AbstractEntityFactory.Args {
    }
}
