package ru.phyllosedis.textario.world;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.console.GameCommands;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.logistics.underground.UndergroundMode;
import ru.phyllosedis.textario.production.assembler.AssemblerComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Сохранение мира v1: список размещений (вид, координаты, тир,
 * поворот, руда/режим/рецепт). Склады и прогресс не сохраняются —
 * загруженный мир стартует с пустыми руками.
 */
@Service
@RequiredArgsConstructor
public class SaveService {

    public record SavedEntity(String kind, int x, int y, String tier, int rotation,
                              String extra, String recipe) {
    }

    private final EntityBlueprintService blueprints;
    private final GameCommands commands;
    private final ComponentManager cm;

    private final ObjectMapper mapper = new ObjectMapper();

    public String save(String path) {
        try {
            List<SavedEntity> out = new ArrayList<>();
            for (Map.Entry<Long, EntityBlueprintService.Placement> e
                    : new TreeMap<>(blueprints.placements()).entrySet()) {
                long id = e.getKey();
                EntityBlueprintService.Placement p = e.getValue();
                RotationComponent rotation = cm.get(id, RotationComponent.class);
                AssemblerComponent assembler = cm.get(id, AssemblerComponent.class);
                out.add(new SavedEntity(
                        p.kind(), p.x(), p.y(), p.tier().name(),
                        rotation == null ? 0 : rotation.getSteps(),
                        p.extra() == null ? "" : p.extra(),
                        assembler == null ? "" : assembler.getRecipeId()));
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), out);
            return "OK: сохранено построек: " + out.size() + " в " + path;
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
    }

    public String load(String path) {
        final List<SavedEntity> entities;
        try {
            entities = mapper.readValue(new File(path),
                    mapper.getTypeFactory().constructCollectionType(List.class, SavedEntity.class));
        } catch (Exception e) {
            return "FAIL: не прочитал " + path + ": " + e.getMessage();
        }
        for (long id : new ArrayList<>(blueprints.placements().keySet())) {
            commands.demolishById(id);
        }
        int ok = 0;
        List<String> skipped = new ArrayList<>();
        for (SavedEntity s : entities) {
            String error = place(s);
            if (error == null) {
                ok++;
            } else {
                skipped.add(s.kind() + "@" + s.x() + ":" + s.y() + " (" + error + ")");
            }
        }
        String result = "OK: загружено построек: " + ok + " из " + path;
        if (!skipped.isEmpty()) {
            result += ", пропущено: " + String.join("; ", skipped);
        }
        return result;
    }

    private String place(SavedEntity s) {
        try {
            Tier tier = Tier.valueOf(s.tier());
            switch (s.kind()) {
                case "miner" -> blueprints.createMiner(s.x(), s.y(), tier);
                case "belt" -> blueprints.createBelt(s.x(), s.y(), tier, ResourceType.EARTH, s.rotation());
                case "inserter" -> blueprints.createInserter(s.x(), s.y(), tier, ResourceType.EARTH, s.rotation());
                case "chest" -> blueprints.createChest(s.x(), s.y(), tier);
                case "furnace" -> blueprints.createFurnace(s.x(), s.y(), tier);
                case "splitter" -> blueprints.createSplitter(
                        s.x(), s.y(), tier, ResourceType.EARTH, SplitMode.valueOf(s.extra()), s.rotation());
                case "assembler" -> {
                    blueprints.createAssembler(s.x(), s.y(), tier);
                    if (s.recipe() != null && !s.recipe().isEmpty()) {
                        commands.setRecipe("assembler@" + s.x() + ":" + s.y(), s.recipe());
                    }
                }
                case "underground" -> blueprints.createUnderground(
                        s.x(), s.y(), tier, s.rotation(), UndergroundMode.valueOf(s.extra()));
                case "turret" -> blueprints.createTurret(s.x(), s.y(), tier);
                case "core" -> blueprints.createCore(s.x(), s.y(), tier);
                default -> {
                    return "неизвестный вид '" + s.kind() + "'";
                }
            }
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
