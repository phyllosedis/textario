package ru.phyllosedis.textario.logistics;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;
import ru.phyllosedis.textario.resource.ContentState;


/**
 * Отвечает за физическое состояние продукта (жидкость, твердое тело, газ)
 *
 */
@Getter
@ToString
@AutoFactory(ComponentType.CONTENT_STATE)
public class ContentStateComponent extends Component {

    private final int contentState;

    protected ContentStateComponent(int contentState) {
        super();
        this.contentState = contentState;
    }

    public record Args(ContentState type) implements ComponentArgs<ContentStateComponent> {
        @Override
        public ContentStateComponent instantiate() {
            return new ContentStateComponent(type.ordinal());
        }
    }

}
