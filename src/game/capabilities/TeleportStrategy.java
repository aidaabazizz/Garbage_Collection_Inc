package game.capabilities;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;

import java.util.List;

public interface TeleportStrategy {
    List<Location> generateOptions(GameMap map, int count);
    void teleport(ContractedWorker worker, Location destination, GameMap map);
}
