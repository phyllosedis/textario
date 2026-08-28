package ru.phyllosedis.textario.production.mining;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@ToString
@Getter
@AutoFactory(ComponentType.EXTRACTOR)
public class ExtractionComponent extends Component {
    private final double speed;

    public ExtractionComponent(double speed) {
        this.speed = speed;
    }

    public record Args(double speed) implements ComponentArgs<ExtractionComponent> {
        @Override
        public ExtractionComponent instantiate() {
            return new ExtractionComponent(speed);
        }
    }
}
