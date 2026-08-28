package ru.phyllosedis.textario.logistics.inserter;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.MarkerComponent;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.LOGISTIC)
public class InserterMarkerComponent extends Component implements MarkerComponent {
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
