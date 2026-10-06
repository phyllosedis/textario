package ru.phyllosedis.textario.combat;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.ENEMY)
public class EnemyComponent extends Component {

    private final int damage;
    private final double speed;

    protected EnemyComponent(int damage, double speed) {
        super();
        this.damage = damage;
        this.speed = speed;
    }

    public record Args(int damage, double speed) implements ComponentArgs<EnemyComponent> {
        @Override
        public EnemyComponent instantiate() {
            return new EnemyComponent(damage, speed);
        }
    }
}
