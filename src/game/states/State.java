package game.states;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.ChickenState;

/**
 * Interface for state behavior in the State pattern.
 * Each state defines its own behavior and transition logic.
 *
 * @author Aida
 */
public interface State {

    /**
     * Returns the action to be performed by the actor in this state.
     */
    Action getAction(Actor actor, Location location);

    /**
     * Determines the next state based on the current context.
     * Must be deterministic (no randomness).
     * Must transition to at least 2 different states (or stay).
     */
    ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState);

    /**
     * Called immediately when entering this state.
     * Must have immediate effect with complex logic.
     */
    void onEnter(Actor actor, Location location);

    /**
     * Called immediately when exiting this state.
     */
    void onExit(Actor actor, Location location);

    /**
     * Returns the name of this state for display.
     */
    String getStateName();
}
