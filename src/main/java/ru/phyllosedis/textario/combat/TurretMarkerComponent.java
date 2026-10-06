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
public class TurretMarkerComponent extends Component implements MarkerComponent {
    protected TurretMarkerComponent() {
    }

    public record Args() implements ComponentArgs<TurretMarkerComponent> {
        @Override
        public TurretMarkerComponent instantiate() {
            return new TurretMarkerComponent();
        }
    }
}
