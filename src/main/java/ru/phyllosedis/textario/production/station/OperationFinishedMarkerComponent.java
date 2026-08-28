package ru.phyllosedis.textario.production.station;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.MarkerComponent;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

/**
 * Флаг вешается на сущность, когда её производственный цикл достиг 100%.
 * На следующем тике другие системы обработают этот флаг и снимут его.
 */
@Getter
@ToString
@AutoFactory(ComponentType.OPERATION_FINISHED)
public class OperationFinishedMarkerComponent extends Component implements MarkerComponent {
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
