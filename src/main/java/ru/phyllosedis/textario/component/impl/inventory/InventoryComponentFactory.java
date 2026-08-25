package ru.phyllosedis.textario.component.impl.inventory;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.factory.ComponentFactory;
import ru.phyllosedis.textario.type.ComponentType;

@Component
public class InventoryComponentFactory extends ComponentFactory<InventoryComponent, InventoryComponent.Args> {

    public InventoryComponentFactory() {
        super(ComponentType.INVENTORY);
    }

    @Override
    public Class<InventoryComponent.Args> getArgsClass() {
        return InventoryComponent.Args.class;
    }

    @Override
    public InventoryComponent create(InventoryComponent.Args componentArgs) {
        return new InventoryComponent(
                componentArgs.size(),
                componentArgs.stackSize(),
                componentArgs.slots().stream()
                .map(e -> new InventoryComponent.Slot(e.type().ordinal(), e.count()))
                .toList());
    }
}
