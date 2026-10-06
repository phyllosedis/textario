package ru.phyllosedis.textario.combat;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.AssociatedMarker;
import ru.phyllosedis.textario.engine.ecs.entity.AbstractEntityFactory;
import ru.phyllosedis.textario.production.ProgressComponent;

@Component
@AssociatedMarker(EnemyMarkerComponent.class)
public class EnemyFactory extends AbstractEntityFactory<EnemyFactory.Args> {

    public EnemyFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        super(cm, cfm, bf);
    }

    @Override
    public void create(Args args) {
        super.create(args);

        long id = args.getId();

        cm.add(id, cfm.create(new EnemyMarkerComponent.Args()));
        cm.add(id, cfm.create(new EnemyComponent.Args(args.getDamage(), args.getSpeed())));
        cm.add(id, cfm.create(new ProgressComponent.Args(0)));
        cm.add(id, cfm.create(new HealthComponent.Args(args.getHp(), args.getHp())));
    }

    @Getter
    @SuperBuilder
    public static class Args extends AbstractEntityFactory.Args {
        private final int hp;
        private final int damage;
        private final double speed;
    }
}
