package ru.phyllosedis.textario.engine.balance;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.resource.Tier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BalanceFactory {
    private final Map<Class<? extends AbstractBalance>, AbstractBalance<?>> registry = new ConcurrentHashMap<>();

    public BalanceFactory(List<AbstractBalance<?>> balances) {
        for (AbstractBalance<?> balance : balances) {
            registry.put(balance.getClass(), balance);
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends AbstractBalance.AbstractStats, B extends AbstractBalance<C>> C getStats(Class<B> clazz, Tier tier) {
        AbstractBalance<?> balance = registry.get(clazz);

        if (balance == null) {
            throw new IllegalArgumentException("Игровые характеристики баланса для класса " + clazz.getSimpleName() + " не найдены.");
        }

        return ((AbstractBalance<C>) balance).stats(tier);
    }
}
