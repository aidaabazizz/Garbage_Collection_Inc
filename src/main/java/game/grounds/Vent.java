package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.*;
import game.actions.CutAction;
import game.capabilities.Cuttable;
import game.capabilities.PoisonStatus;
import game.enums.Ability;
import game.items.IndustrialFan;
import game.managers.Spawner;
import game.utils.SpatialSearch;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A motion-activated biological spawner found within the facility.
 * Unlike standard holes, Vents only trigger when a worker is detected in the
 * immediate surrounding tiles. As per Requirement 4, a successful spawn from a
 * vent results in a toxic cloud that poisons the newly created creature and
 * one random adjacent actor for five turns.
 *
 * @author Chathya Attanayake
 * @author Victoria Tay Wen Xie (modified by)
 * @version 2.0
 */
public class Vent extends Ground implements Cuttable {
    /** Random number generator for spawning decisions and target selection. */
    private final Random rand = new Random();
    /** The number of turns the poison status effect persists. */
    private static final int POISON_DURATION = 5;
    /** The spawning service used to handle creature creation and side effects. */
    private final Spawner spawner;

    /**
     * Constructs a new Vent instance with a reference to the global spawner.
     *
     * @param spawner The spawner manager used to handle Parasite and Slime creation.
     */
    public Vent(Spawner spawner) {
        super('V', "Vent");
        this.spawner = spawner;
        this.enableAbility(Ability.CUTTABLE);
    }

    /**
     * Updates the vent's state every turn.
     * The vent triggers a spawn attempt if at least one worker is adjacent and the
     * vent tile itself is unoccupied.
     *
     * @param location The map location of the vent.
     */
    @Override
    public void tick(Location location) {
        // Motion activated trigger (checks for nearby actors with WORKER ability)
        if (!SpatialSearch.getNearbyWorkers(location).isEmpty() && !location.containsAnActor()) {
            List<Location> validSpawnLocations = new ArrayList<>();
            for (Exit exit : location.getExits()) {
                Location adjacentLoc = exit.getDestination();
                // Ensure the tile doesn't have an actor and can be entered safely
                if (!adjacentLoc.containsAnActor() && adjacentLoc.getGround().canActorEnter(null)) {
                    validSpawnLocations.add(adjacentLoc);
                }
            }

            if (!validSpawnLocations.isEmpty()) {
                // Select an adjacent tile randomly
                Location targetSpawnLocation = validSpawnLocations.get(rand.nextInt(validSpawnLocations.size()));

                // 4. Spawn the creature at the ADJACENT location tile instead of the vent
                if (rand.nextBoolean()) {
                    spawner.spawnParasite(targetSpawnLocation);
                } else {
                    spawner.spawnSlime(targetSpawnLocation);
                }

                // 5. Apply toxic cloud side effects across the surrounding zone
                applyPoison(location, targetSpawnLocation);
            }
        }
    }

    /**
     * Applies a poison status to the newly spawned creature and one random adjacent actor.
     *  Successful spawning triggers a toxic reaction that inflicts 1 damage per turn for 5 turns.
     *
     * @param location The location of the vent where the spawn occurred.
     */
    private void applyPoison(Location location, Location spawnLocation) {
        // 1. Poison the newly spawned creature occupying the vent tile
        if (spawnLocation.containsAnActor()) {
            spawnLocation.getActor().addStatus(new PoisonStatus(POISON_DURATION));
        }
        // Poison exactly one random adjacent actor
        List<Actor> nearby = SpatialSearch.getNearbyActors(location);
        // Select and poison one random target from the collected adjacent actors
        if (!nearby.isEmpty()) {
            Actor target = nearby.get(rand.nextInt(nearby.size()));
            target.addStatus(new PoisonStatus(POISON_DURATION));
        }
    }

    /**
     * Executes the cutting logic which replaces this vent with a walkable Floor tile,
     * permanently making it inactive.
     *
     * @param actor The actor performing the cut action.
     * @param map The current game map containing the vent.
     * @param targetLocation The exact map coordinates where this vent ground sits.
     * @return A description of the destroyed ventilation asset.
     */
    @Override
    public String executeCut(Actor actor, GameMap map, Location targetLocation) {
        targetLocation.setGround(new Floor());
        targetLocation.addItem(new IndustrialFan(this.spawner));
        return actor + " cuts through the vent, vent becomes industrial fan.";
    }

    /**
     * Evaluates adjacent workers and create a cut action option if they are carrying a Plasma Cutter.
     *
     * @param actor The actor looking at the vent tile.
     * @param location The location coordinates of the vent tile.
     * @param direction The orientation descriptive label string.
     * @return A list of valid actions selectable by the adjacent actor.
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = super.allowableActions(actor, location, direction);

        if (this.canBeCut(actor)) {
            actions.add(new CutAction(this, "Vent", location));
        }
        return actions;
    }
}