package ru.phyllosedis.textario.service.factory;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.balance.BalanceFactory;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.type.Tier;

public abstract class AbstractEntityFactory {
    protected final ComponentManager cm;
    protected final ComponentFactoryManager cfm;
    protected final BalanceFactory bf;

    public AbstractEntityFactory(ComponentManager cm, ComponentFactoryManager cfm, BalanceFactory bf) {
        this.cm = cm;
        this.cfm = cfm;
        this.bf = bf;
    }

    public abstract void create(long id, int tier);

    public Tier getTier(int tierInt) {
        Tier tier;
        try {
            tier = Tier.UNDEFINED.getByOrdinal(tierInt);
        } catch (Exception e) {
            throw new IllegalArgumentException("Невозможно создать " + getClass().getName() + " уровня " + tierInt);
        }
        return tier;
    }

    @PostConstruct
    private void validateAnnotations() {
        if (!this.getClass().isAnnotationPresent(Component.class)) {
            throw new IllegalStateException(
                    "Критическая ошибка: Класс " + this.getClass().getSimpleName() +
                            " наследует AbstractEntityFactory, но не помечен аннотацией @Component!"
            );
        }
    }
}
