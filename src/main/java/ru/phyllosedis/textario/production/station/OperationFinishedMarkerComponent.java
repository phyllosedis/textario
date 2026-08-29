package ru.phyllosedis.textario.production.station;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.*;

/**
 * Флаг вешается на сущность, когда её производственный цикл достиг 100%.
 * На следующем тике другие системы обработают этот флаг и снимут его.
 */
@Getter
@ToString
@AutoFactory(ComponentType.OPERATION_FINISHED)
public class OperationFinishedMarkerComponent extends Component implements MarkerComponent {
    private final int completedCycles;

    protected OperationFinishedMarkerComponent(int completedCycles) {
        this.completedCycles = completedCycles;
    }

    public record Args(int completedCycles) implements ComponentArgs<OperationFinishedMarkerComponent> {
        @Override
        public OperationFinishedMarkerComponent instantiate() {
            return new OperationFinishedMarkerComponent(completedCycles);
        }
    }
}
