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
     * Constructor for FleshyTreeStage instances that require growth thresholds.
     *
     * @param spawner   The spawning service used to manage entity generation for this stage.
     * @param name      The descriptive name of the growth stage.
     * @param threshold The number of game turns required before maturation is attempted.
     * @param chance    The probability of successful maturation upon reaching the threshold.
     */
    public FleshyTreeStage(Spawner spawner, String name, char displayChar, int threshold, double chance) {
        super(name, displayChar, threshold, chance); // Pass to AbstractTreeStage
        this.spawner = spawner;
    }

    /**
     * Constructor for FleshyTreeStage instances that do not evolve further (terminal stages).
     *
     * @param spawner The spawning service used to manage entity generation for this stage.
     * @param name    The descriptive name of the growth stage.
     */
    public FleshyTreeStage(Spawner spawner, String name, char displayChar) {
        super(name, displayChar);
        this.spawner = spawner;
    }
}
