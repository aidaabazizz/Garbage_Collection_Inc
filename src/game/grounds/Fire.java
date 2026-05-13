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
 * @version 2.0
 */
public class Fire extends Ground implements FireStackable {

    private final List<Integer> stacks = new ArrayList<>();
    private final Ground previousGround;
    private int age = 0;
    /** The duration of the burning side effect applied to actors for the new requirements.*/
    private static final int BURNING_SIDE_EFFECT_DURATION = 2;
    /** The duration of the burning status effect applied to actors. */
    private static final int BURNING_EFFECT_DURATION = 5;

    /**
     * Overloaded constructor for Lantern/CRT leaks.
     * Uses a default duration of 3, so it survives the disguise round + 2 active rounds.
     */
    public Fire(Ground previousGround) {
        this(previousGround, BURNING_SIDE_EFFECT_DURATION);
    }

    /**
     * Constructs a new Fire instance with custom duration
     * @param previousGround the ground type that existed before fire was created,
     * @param duration the duration of the number of turns this fire stack should lsat
     */
    public Fire(Ground previousGround, int duration) {
        super('^', "Fire");
        this.previousGround = previousGround;
        this.stacks.add(duration);
    }

    /**
     * Returns the display character for this ground.
     * During the first turn of existence, this will ensure that the map will
     * display the character of the previous ground to create a subtle appearance effect.
     * After that only will it display the fire character '^'.
     *
     * @return the character to display for this ground
     */
    @Override
    public char getDisplayChar() {
        if (age < 1) {
            return previousGround.getDisplayChar();
        }
        return super.getDisplayChar();
    }

    /**
     * This will handle the per-turn behaviour of the fire ground.
     * For each turn, it will increment the age counter. It will skip processing on the first turn.
     * If an actor is present on the location, applies a burning status effect for each active fire stack
     * @param location The location of the Ground
     */
    @Override
    public void tick(Location location) {
        age++;
        if (age <= 1) {
            return;
        }
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            for (int i = 0; i < stacks.size(); i++) {
                actor.addStatus(new BurningStatus(BURNING_EFFECT_DURATION));
            }
        }
        for (int i = 0; i < stacks.size(); i++) {
            stacks.set(i, stacks.get(i) - 1);
        }
        stacks.removeIf(turns -> turns <= 0);
        if (stacks.isEmpty()) {
            location.setGround(previousGround);
        }
    }

    /**
     * Adds a new fire stack to this tile.
     * Each new stack burns for 2 turns.
     */
    @Override
    public void addStack() {
        this.stacks.add(BURNING_SIDE_EFFECT_DURATION);
    }

    /**
     * This determines if an actor can enter this tile.
     * @param actor the Actor to check
     * @return True always as actor can enter tiles that are fire type
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }
}