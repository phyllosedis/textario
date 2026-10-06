package ru.phyllosedis.textario.storage;

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
public class ChestMarkerComponent extends Component implements MarkerComponent {
    protected ChestMarkerComponent() {
    }

    public record Args() implements ComponentArgs<ChestMarkerComponent> {
        @Override
        public ChestMarkerComponent instantiate() {
            return new ChestMarkerComponent();
        }
    }
}
