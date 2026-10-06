package ru.phyllosedis.textario.combat;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;
import ru.phyllosedis.textario.resource.ResourceType;

@Getter
@ToString
@AutoFactory(ComponentType.TURRET)
public class TurretComponent extends Component {

    private final int range;
    private final int damage;
    private final int interval;
    private final int ammo;

    protected TurretComponent(int range, int damage, int interval, int ammo) {
        super();
        this.range = range;
        this.damage = damage;
        this.interval = interval;
        this.ammo = ammo;
    }

    public record Args(int range, int damage, int interval, ResourceType ammo)
            implements ComponentArgs<TurretComponent> {
        @Override
        public TurretComponent instantiate() {
            return new TurretComponent(range, damage, interval, ammo.ordinal());
        }
    }
}
