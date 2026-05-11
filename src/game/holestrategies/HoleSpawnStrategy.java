package game.holestrategies;

import edu.monash.fit2099.engine.positions.Location;
import game.managers.Spawner;

//req4
public interface HoleSpawnStrategy {
    boolean spawn(Location location, Spawner spawner);
    HoleSpawnStrategy cloneStrategy();
}
