package game.holestrayergies;
import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

import java.util.Random;

//req4
public class StandardHoleStrategy implements HoleSpawnStrategy {

    @Override
    public void spawn(Location location, Spawner spawner) {
        if (new Random().nextBoolean()) spawner.spawnUndead(location);
        else spawner.spawnSlime(location);
    }
}