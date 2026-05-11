package game.stages;

import game.managers.Spawner;

/**
 * An intermediate abstract class for all growth stages of a Fleshy Tree.
 * This class centralizes the Spawner dependency, ensuring that all stages
 * in the Fleshy Tree lifecycle have access to the spawning service required
 * for biological hazards.
 *
 * This design satisfies the Interface Segregation Principle by ensuring
 * only fleshy-type trees are aware of the Spawner, while other tree types
 * (like Warper Trees) remain independent.
 *
 * @author Jewell Gomes
 */
public abstract class FleshyTreeStage extends AbstractTreeStage{
    /**
     * The spawning manager used to handle creature creation and
     * Requirement 4 environmental side effects.
     */
    protected final Spawner spawner;
    /**
     * Constructor for FleshyTreeStage.
     *
     * @param spawner The spawning service to be used by this stage or
     *                passed to the subsequent growth stage.
     */
    public FleshyTreeStage(Spawner spawner) {
        this.spawner = spawner;
    }
}
