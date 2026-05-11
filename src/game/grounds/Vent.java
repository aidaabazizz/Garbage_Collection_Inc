package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.*;
import game.enums.Ability;
import game.capabilities.PoisonStatus;
import game.managers.CreatureSpawner;
import game.managers.Spawner;
import game.utils.SpatialSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Vent extends Ground {
    private final Random rand = new Random();
    private static final int POISON_DURATION = 5;
    private final Spawner spawner;

    public Vent(Spawner spawner) {
        super('V', "Vent");
        this.spawner = spawner;
    }

    @Override
    public void tick(Location location) {
        // REQ4 Clarification: No cooldown. Triggers if Worker is adjacent.
        if (!SpatialSearch.getNearbyWorkers(location).isEmpty() && !location.containsAnActor()) {
            if (rand.nextBoolean()) spawner.spawnParasite(location);
            else spawner.spawnSlime(location);

            applyPoison(location);
        }
    }

    private boolean isWorkerAdjacent(Location loc) {
        for (Exit exit : loc.getExits()) {
            if (exit.getDestination().containsAnActor() &&
                    exit.getDestination().getActor().hasAbility(Ability.WORKER)) return true;
        }
        return false;
    }

    private void applyPoison(Location loc) {
        // Poison the new spawn
        if (loc.containsAnActor()) loc.getActor().addStatus(new PoisonStatus(POISON_DURATION));

        // REQ4 Clarification: Poison any (one random) adjacent actor
        List<Actor> adjacentActors = new ArrayList<>();
        for (Exit exit : loc.getExits()) {
            if (exit.getDestination().containsAnActor()) {
                adjacentActors.add(exit.getDestination().getActor());
            }
        }
        if (!adjacentActors.isEmpty()) {
            adjacentActors.get(rand.nextInt(adjacentActors.size())).addStatus(new PoisonStatus(POISON_DURATION));
        }
    }
}