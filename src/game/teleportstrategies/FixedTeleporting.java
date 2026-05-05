package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.capabilities.TeleportStrategy;

import java.util.List;

public class FixedTeleporting implements TeleportStrategy {

    private Location fixedLocation;

    public FixedTeleporting(Location fixedLocation) {
        this.fixedLocation = fixedLocation;
    }

    @Override
    public List<Location> generateOptions(GameMap map, int count) {
        return List.of(fixedLocation);
    }

    @Override
    public void teleport(ContractedWorker worker, Location destination, GameMap map) {
        map.moveActor(worker, fixedLocation);
    }
}
