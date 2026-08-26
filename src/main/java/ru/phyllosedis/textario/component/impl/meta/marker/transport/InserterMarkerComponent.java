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
public class InserterMarkerComponent extends MarkerComponent {
    protected InserterMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<InserterMarkerComponent> {
        @Override
        public InserterMarkerComponent instantiate() {
            return new InserterMarkerComponent();
        }
    }
}
