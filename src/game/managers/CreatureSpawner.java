package game.managers;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.actors.Undead;

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

                location.addActor(undead);
            }
        } catch (GameEngineException e) {
            System.err.println("Failed to spawn Undead: " + e.getMessage());
        }
    }
}
