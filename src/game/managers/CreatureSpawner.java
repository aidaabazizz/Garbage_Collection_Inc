package game.managers;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Parasite;
import game.actors.Slime;
import game.actors.Undead;
import game.enums.Ability;
import game.actors.CrazyChicken;

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

    @Override
    public boolean spawnSlime(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;
        display.println("!!! A Slime has emerged at " + spot + " !!!");

        try {
            spot.addActor(new Slime());
            // REACTION: Adjacent workers drop all items
            for (Exit exit : spot.getExits()) {
                Location adj = exit.getDestination();
                if (adj.containsAnActor() && adj.getActor().hasAbility(Ability.WORKER)) {
                    Actor worker = adj.getActor();
                    display.println(">>> " + worker + " is terrified and dropped all items!");
                    List<Item> items = new ArrayList<>(worker.getInventory().getItems());
                    for (Item item : items) {
                        worker.getInventory().remove(item);
                        adj.addItem(item);
                    }
                }
            }
            return true;
        } catch (GameEngineException e) {
            return false;
        }
    }

    @Override
    public boolean spawnUndead(Location center) {
        Location spot = getSpawnLocation(center);
        if (spot == null) return false;

        try {
            Undead undead = new Undead();
            // REACTION: Max HP bonus (+1 for every adjacent creature)
            // Replaced Lambda/Stream with a standard for-loop
            int count = 0;
            for (Exit exit : spot.getExits()) {
                if (exit.getDestination().containsAnActor()) {
                    count++;
                }
            }

            if (count > 0) {
                undead.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, count);
                undead.heal(count);
                display.println("!!! An Undead spawned at " + spot + " with a +" + count + " HP bonus !!!");
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
    @Override
    public void spawnCrazyChicken(Location location) {
        try {
            if (!location.containsAnActor()) {
                location.addActor(new CrazyChicken());
            }
        } catch (GameEngineException ignored) {}
    }
}


