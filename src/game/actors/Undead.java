package game.actors;


import game.BasicInventory;
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
 */
public class Undead extends NonPlayerCharacter {
    private static final int INITIAL_HEALTH = 15;
    private static final int WANDER_PRIORITY = 999;
    private static final int ATTACK_PRIORITY = 1;
    private static final int HUNT_PRIORITY = 10;
    private static final int PUNCH_HIT_RATE = 10;
    private static final int PUNCH_DAMAGE = 1;

    /**
     * Constructor for the Undead class.
     * Initializes the Undead with its name, display character, and health.
     * Configures the intrinsic weapon for combat and sets the behavior
     * priorities for attacking, hunting, and wandering.
     */
    public Undead() {
        super("Undead", 'Ѫ',INITIAL_HEALTH , new BasicInventory());
        this.behaviours.put(ATTACK_PRIORITY, new AttackBehaviour());
        this.behaviours.put(HUNT_PRIORITY, new HuntBehaviour());
        this.behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
        this.setIntrinsicWeapon(new UndeadFist(PUNCH_DAMAGE, PUNCH_HIT_RATE));
    }
}
