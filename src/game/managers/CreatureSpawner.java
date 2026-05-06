package game.managers;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Parasite;
import game.actors.Slime;
import game.actors.Undead;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.List;

/**
 * A manager class responsible for spawning creatures on the moon maps.
 * Implements the specific side effects required by Requirement 4.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake
 */
public class CreatureSpawner implements Spawner{

    /**
     * Attempts to spawn a Slime at the specified location.
     * This method validates that the target location is unoccupied before
     * adding the actor. It further triggers the environmental reaction where
     * adjacent workers are forced to drop their inventory items.
     *
     * @param location The map location where the Slime should be created.
     */
    @Override
    public void spawnSlime(Location location) {
        try {
            // Check if tile is empty first to be safe
            if (!location.containsAnActor()) {
                location.addActor(new Slime());

                //REQ 4: TO IMPLEMENT (ALL WORKERS IN ADJACENT TILES DROP ALL ITEMS) (CHATHYA)
                for (Exit exit : location.getExits()) {
                    Location adj = exit.getDestination();
                    if (adj.containsAnActor() && adj.getActor().hasAbility(Ability.WORKER)) {
                        Actor worker = adj.getActor();
                        List<Item> items = new ArrayList<>(worker.getInventory().getItems());
                        for (Item item : items) {
                            worker.getInventory().remove(item);
                            adj.addItem(item);
                        }
                    }
                }
            }
        } catch (GameEngineException e) {
            System.err.println("Failed to spawn Slime: " + e.getMessage());
        }
    }

    /**
     * Attempts to spawn an Undead entity at the specified location.
     * This method validates that the target location is unoccupied before
     * adding the actor. It triggers the biological reaction where the
     * new Undead receives a permanent health bonus for every creature
     * currently in its adjacent surroundings.
     *
     * @param location The map location where the Undead should be created.
     */
    @Override
    public void spawnUndead(Location location) {
        try {
            if (!location.containsAnActor()) {
                Undead undead = new Undead();

                //REQ 4: MAX HP INCREASED BY 1 FOR EVERY ADJACENT CREATURE (CHATHYA)
                int count = 0;
                for (Exit exit : location.getExits()) {
                    if (exit.getDestination().containsAnActor()) count++;
                }
                if (count > 0) {
                    undead.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, count);
                    undead.heal(count);
                }
                location.addActor(undead);
            }
        } catch (GameEngineException e) {
            System.err.println("Failed to spawn Undead: " + e.getMessage());
        }

    }

    @Override
    public void spawnParasite(Location location) {
        if (location.containsAnActor()) return;
        try {
            location.addActor(new Parasite());
            // REQ 4 Reaction: Adjacent workers take 2 damage
            for (Exit exit : location.getExits()) {
                Location adj = exit.getDestination();
                if (adj.containsAnActor() && adj.getActor().hasAbility(Ability.WORKER)) {
                    adj.getActor().hurt(2); //magic number pls fix
                }
            }
        } catch (GameEngineException ignored) {}
    }

}
