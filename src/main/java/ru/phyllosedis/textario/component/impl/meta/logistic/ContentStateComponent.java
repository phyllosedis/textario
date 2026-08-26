package ru.phyllosedis.textario.component.impl.meta.logistic;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.ContentType;


/**
 * Отвечает за физическое состояние продукта (жидкость, твердое тело, газ)
 *
 */
@Getter
@ToString
@AutoFactory(ComponentType.CONTENT_STATE)
public class ContentStateComponent extends Component {

    private final int contentType;

    protected ContentStateComponent(int contentType) {
        super();
        this.contentType = contentType;
    }

    public record Args(ContentType type) implements ComponentArgs<ContentStateComponent> {
        @Override
        public ContentStateComponent instantiate() {
            return new ContentStateComponent(type.ordinal());
        }
    }

}
