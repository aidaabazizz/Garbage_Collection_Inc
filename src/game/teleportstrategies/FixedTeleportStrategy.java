package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.capabilities.TeleportStrategy;

import java.util.List;

public class FixedTeleportStrategy implements TeleportStrategy {

    private final List<Location> destinations;

    public FixedTeleportStrategy(List<Location> destinations) {
        this.destinations = destinations;
    }
    @Override
    public List<Location> generateOptions(GameMap map, Actor actor, int count) {
        return destinations;
    }

    @Override
    public void teleport(ContractedWorker worker, Location destination, GameMap map) {
        map.moveActor(worker, destination);
    }
}
