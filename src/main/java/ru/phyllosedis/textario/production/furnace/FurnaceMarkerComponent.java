package ru.phyllosedis.textario.production.furnace;

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
public class FurnaceMarkerComponent extends Component implements MarkerComponent {
    protected FurnaceMarkerComponent() {
    }

    public record Args() implements ComponentArgs<FurnaceMarkerComponent> {
        @Override
        public FurnaceMarkerComponent instantiate() {
            return new FurnaceMarkerComponent();
        }
    }
}
