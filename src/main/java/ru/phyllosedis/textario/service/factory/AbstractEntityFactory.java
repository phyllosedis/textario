package ru.phyllosedis.textario.service.factory;

import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;

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
}