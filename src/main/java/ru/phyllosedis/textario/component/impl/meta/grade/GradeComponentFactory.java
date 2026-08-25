package ru.phyllosedis.textario.component.impl.meta.grade;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.factory.ComponentFactory;
import ru.phyllosedis.textario.type.ComponentType;

@Component
public class GradeComponentFactory extends ComponentFactory<GradeComponent, GradeComponent.Args> {
    @Override
    public Class<GradeComponent.Args> getArgsClass() {
        return GradeComponent.Args.class;
    }

    public GradeComponentFactory() {
        super(ComponentType.GRADE);
    }

    @Override
    public GradeComponent create(GradeComponent.Args componentArgs) {
        return new GradeComponent(componentArgs.grade());
    }
}
