package ru.phyllosedis.textario.component.impl.meta.marker.state.solid;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.SOLID_STATE)
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
