package ru.phyllosedis.textario.service.facroty;

import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.type.Tier;

public abstract class AbstractTransportFactory<С extends Component> {

    protected final Class<С> type;

    protected final ComponentManager cm;
    protected final ComponentFactoryManager cfm;

    public AbstractTransportFactory(ComponentManager cm, ComponentFactoryManager cfm, Class<С> type) {
        this.type = type;
        this.cm = cm;
        this.cfm = cfm;
    }

    public abstract void create(long id, int tier);

    public Tier getTier(int tierInt) {
        Tier tier;
        try {
            tier = Tier.UNDEFINED.getByOrdinal(tierInt);
        } catch (Exception e) {
            throw new IllegalArgumentException("Невозможно создать " + type.getName() + " уровня " + tierInt);
        }
        return tier;
    }
}
