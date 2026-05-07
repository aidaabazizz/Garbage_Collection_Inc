package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.*;
import game.enums.Ability;
import game.capabilities.PoisonStatus;
import game.managers.CreatureSpawner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Vent extends Ground {
    private final Random rand = new Random();
    private static final int POISON_DURATION = 5;

    public Vent() { super('V', "Vent"); }

    @Override
    public void tick(Location location) {
        // REQ4 Clarification: No cooldown. Triggers if Worker is adjacent.
        if (isWorkerAdjacent(location) && !location.containsAnActor()) {
            CreatureSpawner spawner = new CreatureSpawner();
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