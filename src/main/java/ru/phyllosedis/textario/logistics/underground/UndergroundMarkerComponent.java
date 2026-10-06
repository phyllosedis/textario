package ru.phyllosedis.textario.logistics.underground;

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
public class UndergroundMarkerComponent extends Component implements MarkerComponent {
    protected UndergroundMarkerComponent() {
    }

    public record Args() implements ComponentArgs<UndergroundMarkerComponent> {
        @Override
        public UndergroundMarkerComponent instantiate() {
            return new UndergroundMarkerComponent();
        }
    }
}
