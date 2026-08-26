package ru.phyllosedis.textario.component.impl.meta.station.marker;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

/**
 * Флаг вешается на сущность, когда её производственный цикл достиг 100%.
 * На следующем тике другие системы обработают этот флаг и снимут его.
 */
@Getter
@ToString
@AutoFactory(ComponentType.OPERATION_FINISHED)
public class OperationFinishedMarkerComponent extends Component {
    protected OperationFinishedMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<OperationFinishedMarkerComponent> {
        @Override
        public OperationFinishedMarkerComponent instantiate() {
            return new OperationFinishedMarkerComponent();
        }
    }
}
