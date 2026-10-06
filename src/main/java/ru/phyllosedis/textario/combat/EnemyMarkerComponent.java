package ru.phyllosedis.textario.combat;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.MarkerComponent;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.MARKER)
public class EnemyMarkerComponent extends Component implements MarkerComponent {
    protected EnemyMarkerComponent() {
    }

    public record Args() implements ComponentArgs<EnemyMarkerComponent> {
        @Override
        public EnemyMarkerComponent instantiate() {
            return new EnemyMarkerComponent();
        }
    }
}
