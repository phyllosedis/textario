package ru.phyllosedis.textario.combat;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

/**
 * Прочность: есть у ядра, турелей и врагов. Кончилось — сущность гибнет.
 * Всё остальное (ленты, буры) враги игнорируют и проходят насквозь.
 */
@Getter
@ToString
@AutoFactory(ComponentType.HEALTH)
public class HealthComponent extends Component {

    private final int hp;
    private final int maxHp;

    protected HealthComponent(int hp, int maxHp) {
        super();
        this.hp = hp;
        this.maxHp = maxHp;
    }

    public record Args(int hp, int maxHp) implements ComponentArgs<HealthComponent> {
        @Override
        public HealthComponent instantiate() {
            return new HealthComponent(hp, maxHp);
        }
    }
}
