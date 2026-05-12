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
    protected final Display display = new Display();
    private int age = 0;

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
     * @param threshold The number of turns required to attempt maturation.
     * @param stageName The display name of the current growth stage for console output.
     */
    protected void updateAge(Location location, int threshold, String stageName) {
        if (this.age < threshold) {
            this.age++;
        }
        display.println(String.format("%s at %s current age: (%d/%d)", stageName, location, age, threshold));
    }

    /**
     * Checks if the tree has reached the threshold and rolls for growth.
     * Use this only when no other action (like spawning) has occurred.
     *
     * @param threshold The age required to trigger a growth roll.
     * @param chance    The probability (0.0 to 1.0) of a successful maturation.
     * @param location  The current map location of the tree for failure notifications.
     * @param stageName The display name of the stage for failure notifications.
     * @return true if the growth roll succeeded; false otherwise.
     */
    protected boolean checkGrowthThreshold(int threshold, double chance, Location location, String stageName) {
        if (age >= threshold) {
            age = 0; // Reset counter after reaching threshold
            if (random.nextDouble() <= chance) {
                return true;
            } else {
                display.println(String.format("%s at %s failed the %.0f%% growth roll. Resetting counter.",
                        stageName, location, chance * 100));
            }
        }
        return false;
    }
}
