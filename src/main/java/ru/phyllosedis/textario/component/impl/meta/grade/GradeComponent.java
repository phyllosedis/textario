package ru.phyllosedis.textario.component.impl.meta.grade;

import lombok.Getter;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.Grade;

@Getter
public class GradeComponent extends Component {
    private final Grade grade;

    protected GradeComponent(Grade grade) {
        super(ComponentType.GRADE);
        this.grade = grade;
    }

    public record Args(Grade grade) implements ComponentArgs<GradeComponent> {
    }
}
