package ru.phyllosedis.textario.logistics.splitter;

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
public class SplitterMarkerComponent extends Component implements MarkerComponent {
    protected SplitterMarkerComponent() {
    }

    public record Args() implements ComponentArgs<SplitterMarkerComponent> {
        @Override
        public SplitterMarkerComponent instantiate() {
            return new SplitterMarkerComponent();
        }
    }
}
