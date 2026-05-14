package game.actors;


import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Infectable;
import game.inventory.BasicInventory;
import game.behaviours.AttackBehaviour;
import game.behaviours.HuntBehaviour;
import game.behaviours.WanderBehaviour;
import game.weapons.UndeadFist;

/**
 * A hostile entity representing the reanimated remains of a former worker.
 * The Undead wanders the facility and aggressively attacks any contractors
 * within its immediate surroundings using its bare fists. It follows a
 * hierarchical behavior system that prioritizes combat over movement.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake (Modified by)
 */
public class Undead extends NonPlayerCharacter implements Infectable {
    /** Initial health points for the Undead. */
    private static final int INITIAL_HEALTH = 15;
    /** Priority level for the wandering behavior. */
    private static final int WANDER_PRIORITY = 999;
    /** Priority level for the attacking behavior. */
    private static final int ATTACK_PRIORITY = 1;
    /** Priority level for the hunting behavior during alarms. */
    private static final int HUNT_PRIORITY = 10;
    /** Percentage chance for the Undead's punch to land. */
    private static final int PUNCH_HIT_RATE = 10;
    /** Damage dealt by the Undead's punch. */
    private static final int PUNCH_DAMAGE = 1;
    /** Extreme damage value used to simulate an instant explosion/death. */
    private static final int INSTANT_DEATH_DAMAGE = 999;


    /**
     * Constructor for the Undead class.
     * Initializes the Undead with its name, display character, and health.
     * Configures the intrinsic weapon for combat and sets the behavior
     * priorities for attacking, hunting, and wandering.
     */
    public Undead() {
        super("Undead", 'Ѫ',INITIAL_HEALTH , new BasicInventory());
        //Behavior hierarchy
        this.behaviours.put(ATTACK_PRIORITY, new AttackBehaviour());
        this.behaviours.put(HUNT_PRIORITY, new HuntBehaviour());
        this.behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
        //Set intrinsic weapon (bare fists)
        this.setIntrinsicWeapon(new UndeadFist(PUNCH_DAMAGE, PUNCH_HIT_RATE));
    }

    /**
     * Defines the reaction of the Undead when infected by a Parasite.
     * Per Requirement 4, this results in an immediate explosion that kills the Undead.
     *
     * @param location The map location where the infection occurs.
     */
    @Override
    public void reactToInfection(Location location) {
        this.hurt(INSTANT_DEATH_DAMAGE);// Instantly blows up

        //Remove from map immediately as it is dead
        location.map().removeActor(this);

    }


}
