package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.List;
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
     * Shared logic for aging and growth checks.
     * @return true if the tree successfully meets the criteria to grow.
     */
    protected boolean incrementAgeAndCheckGrowth(Location location, int threshold, double chance, String stageName) {
        age++;
        display.println(String.format("%s at %s current age: (%d/%d)", stageName, location, age, threshold));

        if (age >= threshold) {
            age = 0; // Reset counter
            if (random.nextDouble() <= chance) {
                return true;
            } else {
                display.println(String.format("%s at %s failed the %.0f%% growth roll. Resetting counter.",
                        stageName, location, chance * 100));
            }
        }
        return false;
    }

    /**
     * Getter for the current age, used by subclasses for display purposes.
     */
    protected int getAge() {
        return this.age;
    }
}
