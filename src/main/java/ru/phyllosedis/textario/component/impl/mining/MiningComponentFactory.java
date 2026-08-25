package ru.phyllosedis.textario.component.impl.mining;

import ru.phyllosedis.textario.component.factory.ComponentFactory;
import ru.phyllosedis.textario.type.ComponentType;

@org.springframework.stereotype.Component
public class MiningComponentFactory extends ComponentFactory<MiningComponent, MiningComponent.Args> {

    public MiningComponentFactory() {
        super(ComponentType.MINING);
    }

    @Override
    public Class<MiningComponent.Args> getArgsClass() {
        return MiningComponent.Args.class;
    }

    @Override
    public MiningComponent create(MiningComponent.Args componentArgs) {
        return new MiningComponent(
                componentArgs.resourceType().ordinal(),
                componentArgs.speed(),
                componentArgs.progress(),
                componentArgs.tier().ordinal());
    }
}
