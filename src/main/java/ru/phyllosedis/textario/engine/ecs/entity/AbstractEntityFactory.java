package ru.phyllosedis.textario.engine.ecs.entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.phyllosedis.textario.engine.balance.BalanceFactory;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.resource.Tier;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.PositionComponent;

public abstract class AbstractEntityFactory<T extends AbstractEntityFactory.Args> {
    protected final ComponentManager cm;
    protected final ComponentFactoryRegistry cfm;
    protected final BalanceFactory bf;

    public AbstractEntityFactory(ComponentManager cm, ComponentFactoryRegistry cfm, BalanceFactory bf) {
        this.cm = cm;
        this.cfm = cfm;
        this.bf = bf;
    }

    public void create(T args) {
        cm.add(args.getId(), cfm.create(new PositionComponent.Args(args.getX(), args.getY())));
        cm.add(args.getId(), cfm.create(new BuildingComponent.Args(args.getWidth(), args.getHeight())));
    }

    @Getter
    @SuperBuilder
    protected abstract static class Args {
        private final long id;
        private final Tier tier;
        private final int x;
        private final int y;
        private final int width;
        private final int height;
    }
}