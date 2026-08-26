package ru.phyllosedis.textario.component.impl.meta.marker.transport;

import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.component.impl.meta.marker.MarkerComponent;

public class BeltMarkerComponent extends MarkerComponent {
    protected BeltMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<BeltMarkerComponent> {
        @Override
        public BeltMarkerComponent instantiate() {
            return null;
        }
    }
}
