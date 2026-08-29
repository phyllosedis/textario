package ru.phyllosedis.textario.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;

@Service
@RequiredArgsConstructor
public class GameInitializer implements CommandLineRunner {

    private final EntityBlueprintService entityBlueprintService;

    @Override
    public void run(String... args) throws Exception {
        long miner = entityBlueprintService.createMiner(2, 4, Tier.ONE, ResourceType.IRON_ORE);
//        long inserter = entityBlueprintService.createInserter(1, 1, Tier.ONE, ResourceType.EARTH);
    }


}
