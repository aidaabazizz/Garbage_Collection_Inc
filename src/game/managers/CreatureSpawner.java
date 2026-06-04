package game.managers;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.DropAction;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.*;
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
    /** The amount of damage dealt to adjacent workers when a Parasite spawns. */
    private static final int PARASITE_SPAWN_DAMAGE = 2;
    /** Terminal interface for displaying spawning events and side effects. */
    private final Display display = new Display();

    /**
     * Finds a valid spot for spawning.
     * Checks the center tile first (for Holes/Vents).
     * If blocked, checks adjacent tiles (for Trees/Infected Workers).
     *
     * @param center the reference location where the spawn is triggered
     * @return a traversable and unoccupied Location if found; null otherwise
     */
    private Location getSpawnLocation(Location center) {
        // Check the trigger tile itself
        if (center.canActorEnter(null)) {
            return center;
        }
        // Check adjacent tiles if the trigger tile is blocked
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
     *
     * @param center the location where the Slime emergence was initiated
     * @return true if the Slime was successfully added to the map; false if spawning failed
     */
    @Override
    public boolean spawnSlime(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;
        try {
            spot.addActor(new Slime());
            display.println("!!! A Slime has emerged at " + spot + " !!!");
            // Trigger inventory drop for adjacent workers
            dropAllItems( spot);
            return true;
        } catch (Exception e) { return false; }
    }


    /**
     * This method creates an undead creature. It gives the undead a
     * health bonus based on the number of other creatures already
     * standing in the surrounding tiles.
     *
     * @param center the location where the Undead emergence was initiated
     * @return true if the Undead was successfully added to the map; false if spawning failed
     */
    @Override
    public boolean spawnUndead(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;

        try {
            Undead undead = new Undead();
            List<Actor> nearbyCreatures = SpatialSearch.getNearbyActors(spot);
            int count = nearbyCreatures.size();
            // Calculate evolution bonus based on non-worker actors
            for (Actor actor : nearbyCreatures) {
                // If the actor does not have the WORKER ability, it is a creature
                if (!actor.hasAbility(Ability.WORKER)) {
                    count++;
                }
            }
            // Apply permanent Max HP increase and heal the new amount
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

    /**
     * Spawns a Parasite and deals immediate damage to adjacent workers.
     * Requirement 4: Adjacent workers take 2 points of damage upon emergence.
     *
     * @param center The location where the Parasite emergence is initiated.
     * @return true if the Parasite was successfully added to the map; false otherwise.
     */
    @Override
    public boolean spawnParasite(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;

        try {
            spot.addActor(new Parasite());
            display.println("!!! A Parasite has emerged at " + spot + " !!!");

            // Identify adjacent workers and apply damage
            List<Actor> targets = SpatialSearch.getNearbyWorkers(spot);
            // Apply damage to every worker found
            for (Actor worker : targets) {
                worker.hurt(PARASITE_SPAWN_DAMAGE);
                display.println(">>> " + worker + " was bitten by the Parasite and took " + PARASITE_SPAWN_DAMAGE + " damage!");
            }

            return true;
        } catch (GameEngineException e) {
            return false;
        }
    }

    /**
     * Forces all workers adjacent to a Slime's spawn location to drop their inventory.
     * This utility supports the Requirement 4 "terrified" reaction.
     *
     * @param slimeSpot The location where the Slime spawned.
     */
    private void dropAllItems(Location slimeSpot) {
        // 1. Find all workers nearby using the shared utility
        List<Actor> nearbyWorkers = SpatialSearch.getNearbyWorkers(slimeSpot);

        for (Actor worker : nearbyWorkers) {
            display.println(">>> " + worker + " is terrified and dropped all items!");

            GameMap map = slimeSpot.map();

            // 3. Create a copy of the inventory
            List<Item> inventoryCopy = new ArrayList<>(worker.getInventory().getItems());

            for (Item item : inventoryCopy) {
                // Execute a DropAction for each item in the worker's inventory
               DropAction dropAction = new DropAction(item);
                String result = dropAction.execute(worker, map);
                display.println("    " + result);
            }
        }
    }

    /**
     * REQ5: Spawns a CrazyChicken at the specified location.
     * The CrazyChicken is a stateful creature with four distinct states:
     * WANDER, MIMICKING, FRENZY, and HUNGRY.
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
            return true;
        } catch (GameEngineException e) {
            return false;
        }
    }

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


