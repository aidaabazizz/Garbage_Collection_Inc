package game.managers;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.*;
import game.capabilities.DisorientedStatus;
import game.enums.Ability;
import game.utils.SpatialSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * A manager class responsible for spawning creatures on the moon maps.
 * Implements the specific side effects required by Requirement 4.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake
 * @author Aida
 */
public class CreatureSpawner implements Spawner {
    private static final int PARASITE_SPAWN_DAMAGE = 2;
    private final Display display = new Display();

    /**
     * Finds a valid spot for spawning.
     * Checks the center tile first (for Holes/Vents).
     * If blocked, checks adjacent tiles (for Trees/Infected Workers).
     */
    private Location getSpawnLocation(Location center) {
        // canActorEnter(null) is the polymorphic check for Ground + Actors
        if (center.canActorEnter(null)) {
            return center;
        }
        for (Exit exit : center.getExits()) {
            Location adj = exit.getDestination();
            if (adj.canActorEnter(null)) {
                return adj;
            }
        }
        return null;
    }

    /**
     * This method creates a slime. When a slime emerges it causes nearby
     * workers to become terrified and drop every item in their inventory
     * onto the ground.
     */
    @Override
    public boolean spawnSlime(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;
        display.println("!!! A Slime has emerged at " + spot + " !!!");
        try {
            spot.addActor(new Slime());

            // USE THE UTILITY: Get all nearby workers
            List<Actor> targets = SpatialSearch.getNearbyWorkers(spot);

            for (Actor worker : targets) {
                display.println(">>> " + worker + " is terrified and dropped all items!");
                dropItems(worker, spot.map().locationOf(worker));
            }
            return true;
        } catch (Exception e) { return false; }
    }


    /**
     * This method creates an undead creature. It gives the undead a
     * health bonus based on the number of other creatures already
     * standing in the surrounding tiles.
     */
    @Override
    public boolean spawnUndead(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;

        try {
            Undead undead = new Undead();
            List<Actor> nearbyCreatures = SpatialSearch.getNearbyActors(spot);
            int count = nearbyCreatures.size();
            if (count > 0) {
                undead.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, count);
                undead.heal(count);
                int newMaxHealth = undead.getStatistic(ActorStatistics.HEALTH);
                display.println(String.format("!!! %s at %s has evolved! Nearby lifeforms increased its Max HP by %d. New Max Health: %d !!!",
                        undead, spot, count, newMaxHealth));
            }

            spot.addActor(undead);
            return true;
        } catch (GameEngineException e) {
            return false;
        }
    }

    @Override
    public boolean spawnParasite(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;

        try {
            spot.addActor(new Parasite());
            display.println("!!! A Parasite has emerged at " + spot + " !!!");
            // REACTION: Adjacent workers take 2 damage
            for (Exit exit : spot.getExits()) {
                Location adj = exit.getDestination();
                if (adj.containsAnActor() && adj.getActor().hasAbility(Ability.WORKER)) {
                    adj.getActor().hurt(PARASITE_SPAWN_DAMAGE);
                    display.println(">>> " + adj.getActor() + " was bitten by the Parasite and took 2 damage!");
                }
            }
            return true;
        } catch (GameEngineException e) {
            return false;
        }
    }

    private void dropItems(Actor actor, Location location) {
        List<Item> inventoryCopy = new ArrayList<>(actor.getInventory().getItems());
        for (Item item : inventoryCopy) {
            actor.getInventory().remove(item);
            location.addItem(item);
        }
    }

    /**
     * REQ5: Spawns a CrazyChicken at the specified location.
     * The CrazyChicken is a stateful creature with four distinct states:
     * WANDER, MIMICKING, FRENZY, and HUNGRY.
     *
     * Environmental Reaction: When a CrazyChicken spawns, all adjacent workers
     * become disoriented by its sudden appearance for 3 turns.
     *
     * @param center The map location where the CrazyChicken should be created.
     * @return true if spawn was successful, false otherwise
     */
    @Override
    public boolean spawnCrazyChicken(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;

        try {
            spot.addActor(new CrazyChicken());
            display.println("A CrazyChicken has emerged at " + spot + "! BUK BUK BUK!");

            // Adjacent workers become disoriented (matches the Slime/Parasite pattern)
            for (Exit exit : spot.getExits()) {
                Location adj = exit.getDestination();
                if (adj.containsAnActor() && adj.getActor().hasAbility(Ability.WORKER)) {
                    Actor worker = adj.getActor();
                    worker.addStatus(new DisorientedStatus(3));
                    display.println(">>> " + worker + " is disoriented by the CrazyChicken!");
                }
            }
            return true;
        } catch (GameEngineException e) {
            return false;
        }
    }
    // Add to CreatureSpawner.java
    @Override
    public boolean spawnElsa(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;

        try {
            spot.addActor(new Elsa());
            display.println("Elsa has emerged at " + spot + "! The air grows cold...");

            for (Exit exit : spot.getExits()) {
                Location adj = exit.getDestination();
                if (adj.containsAnActor()) {
                    display.println(">>> " + adj.getActor() + " feels a sudden chill!");
                }
            }
            return true;
        } catch (GameEngineException e) {
            return false;
        }
    }
}


