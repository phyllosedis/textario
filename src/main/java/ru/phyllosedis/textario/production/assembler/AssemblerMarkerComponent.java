package ru.phyllosedis.textario.production.assembler;

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
public class AssemblerMarkerComponent extends Component implements MarkerComponent {
    protected AssemblerMarkerComponent() {
    }

    public record Args() implements ComponentArgs<AssemblerMarkerComponent> {
        @Override
        public AssemblerMarkerComponent instantiate() {
            return new AssemblerMarkerComponent();
        }
    }
}
