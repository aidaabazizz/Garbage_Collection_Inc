package game.holestrategies;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

import java.util.Random;

//req4
public class ParasiticHoleStrategy implements HoleSpawnStrategy {
    private final Random random = new Random();

    @Override
    public boolean spawn(Location location, Spawner spawner) {
        // UPDATED REQ4: Spawns Undead and Parasites
        if (random.nextBoolean()) {
            return spawner.spawnUndead(location);
        } else {
            return spawner.spawnParasite(location);
        }
    }
}