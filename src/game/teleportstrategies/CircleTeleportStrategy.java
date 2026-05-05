package game.teleportstrategies;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ContractedWorker;
import game.capabilities.TeleportStrategy;

import java.util.List;
import java.util.Random;

public class CircleTeleportStrategy implements TeleportStrategy {

    private Random random= new Random();

    @Override
    public List<Location> generateOptions(GameMap map, Actor actor, int count) {
        return List.of();
    }

    @Override
    public void teleport(ContractedWorker worker, Location destination, GameMap map) {
        map.moveActor(worker, destination);
    }

    public List<Location> generateCircleOptions(ContractedWorker worker, GameMap map, int count){
        Location current = map.locationOf(worker);
        List<Location> nearby = current.getNearbyLocations(radius);

    }
}
