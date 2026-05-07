package game.holestrayergies;

import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

//req4
public interface HoleSpawnStrategy {
    void spawn(Location location, Spawner spawner);
}
