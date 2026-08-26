package ru.phyllosedis.textario.component.impl.meta.marker.transport;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.component.impl.meta.marker.MarkerComponent;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.TRANSPORT)
public class BeltMarkerComponent extends MarkerComponent {
    protected BeltMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<BeltMarkerComponent> {
        @Override
        public BeltMarkerComponent instantiate() {
            return new BeltMarkerComponent();
        }
    }
}
