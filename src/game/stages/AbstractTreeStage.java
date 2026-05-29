package game.stages;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Abstract implementation of TreeStage providing shared utility methods.
 * Fulfills the DRY principle by centralizing worker detection and dependency management.
 *
 * @author Jewell Gomes
 */
public abstract class AbstractTreeStage implements TreeStage {
    /** Random number generator for growth probabilities. */
    protected static final Random random = new Random();
    /** By making this protected, all "Stage" subclasses inherit it automatically. */
    protected static final Display display = new Display();
    /** The age of the current tree stage **/
    private int age = 0;
    /**
     * The successor stage in the tree's lifecycle.
     * This is used to facilitate state transitions without hardcoding specific class names
     * inside the evolution logic.
     */
    protected TreeStage nextStage;
    /** The number of game turns required before an evolution attempt can be made. */
    protected final int threshold;
    /** The probability that the stage will successfully mature upon reaching the threshold. */
    protected final double chance;
    /** The descriptive name of this stage for terminal logs and status updates. */
    protected final String name;
    /** The descriptive symbol of this stage for terminal logs and status updates. */
    protected final char displayChar;

    /**
     * Constructor for stages that undergo growth and evolution.
     *
     * @param name      The descriptive name of this stage.
     * @param threshold The number of turns required to attempt maturation.
     * @param chance    The probability of successful maturation upon reaching the threshold.
     */
    public AbstractTreeStage(String name, char displayChar,int threshold, double chance) {
        this.name = name;
        this.displayChar = displayChar;
        this.threshold = threshold;
        this.chance = chance;
    }

    /**
     * Constructor for terminal stages that do not evolve further (e.g., Mature).
     * Initializes threshold and chance to zero.
     *
     * @param name The descriptive name of this stage.
     */
    public AbstractTreeStage(String name, char displayChar) {
        this(name,displayChar, 0, 0); // Threshold and chance are irrelevant for mature stages
    }

    /**
     * Increments the age counter and prints the status.
     * Need this because Fleshy Sprout has two options to Spawn or to Grow.
     * My implementation is when it spawns it still increments the age.
     * So, need to have a separate update age method, specifically for the
     * Fleshy Sprout.
     * Use this at the very start of execute method to satisfy the
     * Internal State Update happens regardless rule.
     *
     * @param location  The current map location of the tree.
     */
    protected void updateAge(Location location) {
        if (this.age < this.threshold) {
            this.age++;
        }
        display.println(String.format("%s at %s current age: (%d/%d)", name, location, age, threshold));
    }

    /**
     * Checks if the tree has reached the threshold and rolls for growth.
     * Use this only when no other action (like spawning) has occurred.
     *
     * @param location  The current map location of the tree for failure notifications.
     * @return true if the growth roll succeeded; false otherwise.
     */
    protected boolean checkGrowthThreshold(Location location) {
        if (age >= threshold) {
            age = 0; // Reset counter after reaching threshold
            if (random.nextDouble() <= chance) {
                return true;
            } else {
                display.println(String.format("%s at %s failed the %.0f%% growth roll. Resetting counter.",
                        name, location, chance * 100));
            }
        }
        return false;
    }

    /**
     * Injects the next stage in the tree's lifecycle.
     * This method is the core of the Dependency Injection fix, allowing the tree
     * to define its evolution path (e.g., Sprout -> Sapling -> Mature) externally
     * rather than hardcoding those transitions within individual stage classes.
     *
     * @param nextStage The TreeStage instance that should follow the current one.
     */
    public void setNextStage(TreeStage nextStage) {
        this.nextStage = nextStage;
    }

    /**
     * Returns the name of this stage.
     * Facilitates decoupled communication between stages during lifecycle transitions.
     *
     * @return The name string of this stage.
     */
    @Override
    public String toString() { return name; }

    /**
     * This method returns the symnol which is the visual
     * icon used to show a tree on the game world map.
     */
    @Override
    public char getDisplayChar() { return displayChar; }
}

