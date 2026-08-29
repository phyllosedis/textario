package ru.phyllosedis.textario.resource.marker;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.MARKER)
public class SolidStateMarkerComponent extends Component {
    protected SolidStateMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<SolidStateMarkerComponent> {
        @Override
        public SolidStateMarkerComponent instantiate() {
            return new SolidStateMarkerComponent();
        }
    }
}
