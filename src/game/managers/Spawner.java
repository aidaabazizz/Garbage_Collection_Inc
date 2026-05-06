package game.managers;

import edu.monash.fit2099.engine.positions.Location;

public interface Spawner {
    public void spawnSlime(Location location);
    public void spawnUndead(Location location);
}
