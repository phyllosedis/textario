package ru.phyllosedis.textario;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.service.EntityBlueprintService;
import ru.phyllosedis.textario.type.ContentState;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameInitializer implements CommandLineRunner {

    public static final List<Entity> activeEntities = new ArrayList<>();
    private final EntityBlueprintService entityBlueprintService;
    @Override
    public void run(String... args) throws Exception {
        System.out.println("создаём бур");
        long miner = entityBlueprintService.createMiner(2, 4, ResourceType.IRON_ORE, ContentState.SOLID, 1);

    }


}
