package ru.phyllosedis.textario.component.impl.mining;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;

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
