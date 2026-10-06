package ru.phyllosedis.textario.engine.metrics;

import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Счётчики игры и тайминги тика. Дёргают системы (одна строка),
 * читают команда stats и будущие дашборды. Вне тик-бюджета
 * ничего тяжёлого нет — только инкременты.
 */
@Service
public class MetricsService {

    private final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final AtomicLong ticks = new AtomicLong();
    private final long startedAt = System.currentTimeMillis();

    private volatile Map<String, Long> lastTick = Map.of();
    private volatile long lastTickTotal;

    public void count(String metric, ResourceType type, long n) {
        if (n <= 0) {
            return;
        }
        counters.computeIfAbsent(metric + ":" + type.name(), k -> new AtomicLong()).addAndGet(n);
    }

    public long nextTick() {
        return ticks.incrementAndGet();
    }

    public void tickDone(Map<String, Long> perSystemNanos, long totalNanos) {
        lastTick = Map.copyOf(perSystemNanos);
        lastTickTotal = totalNanos;
    }

    public long tickCount() {
        return ticks.get();
    }

    public long get(String metric, ResourceType type) {
        AtomicLong value = counters.get(metric + ":" + type.name());
        return value == null ? 0 : value.get();
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        double elapsedMin = Math.max(1.0 / 60.0, (System.currentTimeMillis() - startedAt) / 60000.0);
        sb.append("ТИК #").append(ticks.get())
                .append(", тик занимает ").append(String.format("%.2f", lastTickTotal / 1000.0)).append(" мкс\n");
        sb.append("системы мкс: ");
        for (Map.Entry<String, Long> e : new TreeMap<>(lastTick).entrySet()) {
            sb.append(e.getKey().replace("System", "")).append("=")
                    .append(String.format("%.0f", e.getValue() / 1000.0)).append(" ");
        }
        sb.append("\n");
        renderGroup(sb, "mined", "ДОБЫТО", elapsedMin);
        renderGroup(sb, "smelted", "ВЫПЛАВЛЕНО", elapsedMin);
        renderGroup(sb, "crafted", "СОБРАНО", elapsedMin);
        renderGroup(sb, "moved", "ПЕРЕНЕСЕНО", elapsedMin);
        return sb.toString();
    }

    private void renderGroup(StringBuilder sb, String metric, String title, double elapsedMin) {
        TreeMap<String, Long> group = new TreeMap<>();
        for (Map.Entry<String, AtomicLong> e : counters.entrySet()) {
            if (e.getKey().startsWith(metric + ":")) {
                group.put(e.getKey().substring(metric.length() + 1), e.getValue().get());
            }
        }
        if (group.isEmpty()) {
            return;
        }
        sb.append(title).append(": ");
        for (Map.Entry<String, Long> e : group.entrySet()) {
            sb.append(e.getKey()).append(" x").append(e.getValue())
                    .append(String.format(" (%.1f/мин)", e.getValue() / elapsedMin)).append(" ");
        }
        sb.append("\n");
    }
}
