package game.actors;

import game.behaviours.InfectBehaviour;
import game.inventory.BasicInventory;
import game.behaviours.WanderBehaviour;

/**
 * A highly invasive biological signature representing a Parasite.
 * As part of Requirement 4, this creature tracks and attempts to infect valid hosts
 * in its immediate surroundings. Upon successful infection, the Parasite perishes,
 * but the host suffers various detrimental effects depending on its nature.
 * It follows a hierarchical behavior system where infection is prioritized over movement.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class Parasite  extends NonPlayerCharacter{
    /** The initial health points of the parasite. */
    private static final int INITIAL_HEALTH = 30;
    /** Priority level for the wandering behavior. */
    private static final int WANDER_PRIORITY = 999;
    /** Priority level for the infection behavior. */
    private static final int INFECT_PRIORITY = 1;

    /**
     * Constructor for the Parasite class.
     * Initializes the Parasite with its name, display character 'x', and health.
     * Configures the behavior priorities to ensure it attempts to infect
     * nearby hosts before choosing to wander.
     */
    public Parasite(){
        super("Parasite",'x',INITIAL_HEALTH, new BasicInventory());
        //Highest priority: infect adjacent valid targets (REQ4)
        this.behaviours.put(INFECT_PRIORITY, new InfectBehaviour());
        //Lowest priority: move randomly if no host is nearby
        this.behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
    }

}
