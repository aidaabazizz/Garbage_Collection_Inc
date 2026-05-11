package game.actors;

import game.behaviours.SingingBehaviour;
import game.capabilities.Hypnotizable;
import game.capabilities.HypnotizedStatus;
import game.inventory.BasicInventory;
import game.behaviours.ConsumeBehaviour;
import game.behaviours.WanderBehaviour;

/**
 * A non-hostile creature that inhabits the moon facility.
 * The Slime is a gluttonous entity that wanders the map with the primary
 * objective of consuming any items found on the ground. It inherits
 * the effects of any item it consumes, such as healing or poisoning.
 *
 * @author Jewell Gomes
 * @author Aida (Modified to impement hypnotizable)
 */
public class Slime extends NonPlayerCharacter implements Hypnotizable {
    private static final int INITIAL_HEALTH = 25;
    private static final int WANDER_PRIORITY = 999;
    private static final int CONSUME_PRIORITY = 1;
    private static final int SINGING_PRIORITY = 2; //REQ5

    /**
     * Constructor for the Slime class.
     * Initializes the Slime with a specific name, display character,
     * hit points, and an empty inventory. It also configures the
     * consumption and wandering behaviors.
     */
    public Slime() {
        super("Slime", '⍾', INITIAL_HEALTH, new BasicInventory());
        this.behaviours.put(CONSUME_PRIORITY, new ConsumeBehaviour());
        this.behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
        this.behaviours.put(SINGING_PRIORITY, new SingingBehaviour()); //REQ5
    }

    @Override
    public void hypnotize(int duration) {
        this.addStatus(new HypnotizedStatus(duration));
    }

}

