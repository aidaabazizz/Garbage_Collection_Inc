package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.BurningStatus;
import game.capabilities.FireStackable;

import java.util.ArrayList;
import java.util.List;

/**
 * A hazardous ground type that causes damage over time.
 * Fire can stack multiple instances of heat on a single tile. Every turn,
 * it applies a burning status to any actor standing on it for every
 * active stack. Once all stacks have expired, the ground reverts
 * to its original state.
 *
 * @author Jewell Gomes
 */
public class Fire extends Ground implements FireStackable {

    /** The duration a fire stack remains on the ground tile. */
    private static final int STACK_DURATION = 5;

    /** The duration of the burning status effect applied to actors. */
    private static final int BURNING_EFFECT_DURATION = 5;

    private final List<Integer> stacks = new ArrayList<>();
    private final Ground previousGround;

    /**
     * Constructs a new Fire instance on top of existing ground.
     * Creates a fire hazard that replaces the specified ground type. The fire
     * initializes with a single stack that will persist for 5 turns before expiring.
     * @param previousGround the ground type that existed before fire was created,
     * which will be restored after all fire stacks expire
     */
    public Fire(Ground previousGround) {
        super('^', "Fire");
        this.previousGround = previousGround;
        this.stacks.add(STACK_DURATION);
    }

    /**
     * Adds a new stack of fire to the current location, resetting
     * its individual duration.
     */
    @Override
    public void addStack() {
        this.stacks.add(STACK_DURATION);
    }

    /**
     * Handles the logic for every game turn. It applies burning statuses
     * to actors at the location, decrements the duration of all stacks,
     * and removes the fire if no stacks remain.
     * @param location The map location where the fire exists.
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            // Apply one burning status per active fire stack
            // For example, if there are 3 stacks, the actor will receive 3 separate burning effects
            // each dealing damage over 5 turns
            for (int i = 0; i < stacks.size(); i++) {
                location.getActor().addStatus(new BurningStatus(BURNING_EFFECT_DURATION));
            }
        }

        // Decrease the remaining duration of each fire stack by 1 turn
        // Each stack starts at 5 turns and counts down to 0
        for (int i = 0; i < stacks.size(); i++) {
            stacks.set(i, stacks.get(i) - 1);
        }

        // Remove any stacks that have reached zero or below
        // This prevents expired stacks from continuing to cause damage
        stacks.removeIf(turns -> turns <= 0);

        // If no fire stacks remain active, the fire has completely died out
        // Restore the original ground that existed before the fire started
        if (stacks.isEmpty()) {
            location.setGround(previousGround);
        }
    }

    /**
     * Allows actors to enter the fire hazard.
     * @param actor The actor entering the fire.
     * @return Always true.
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }
}
