package ru.phyllosedis.textario.production.assembler;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

/**
 * Выбранный рецепт сборки. Пустая строка — рецепт не выбран, станция простаивает.
 */
@Getter
@ToString
@AutoFactory(ComponentType.ASSEMBLER)
public class AssemblerComponent extends Component {

    private final String recipeId;

    protected AssemblerComponent(String recipeId) {
        super();
        this.recipeId = recipeId == null ? "" : recipeId;
    }

    public record Args(String recipeId) implements ComponentArgs<AssemblerComponent> {
        @Override
        public AssemblerComponent instantiate() {
            return new AssemblerComponent(recipeId);
        }
    }
}
