package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.sanctuary.DamageInterceptor;
import game.sanctuary.SanctuaryTool;

/**
 * An action that triggers the deployment of a holy sanctuary field via a  SanctuaryTool.
 * This action facilitates the transition from a portable item (Heaven Token) to a
 * static environmental effect (Sanctuary Field). It leverages the SanctuaryTool interface
 * to adhere to the Dependency Inversion Principle, allowing the action to remain decoupled
 * from the concrete item implementation.
 *
 * @author Chathya Attanayake
 * @version 1.0
 *
 */
public class ActivateHeavenTokenAction extends Action {
    /** The token to consume on use. */
    private final SanctuaryTool tool;

    /** How many turns the sanctuary field lasts. */
    private static final int FIELD_DURATION = 10;

    /**
     * Creates an action to activate the given Heaven Token.
     *
     * @param tool the token being used
     */
    public ActivateHeavenTokenAction(SanctuaryTool tool) {
        this.tool = tool;
    }

    /**
     * Executes the token use: removes the token from inventory, creates a
     * SanctuaryField at the actor's location, and registers it with the
     * map so it ticks each turn independently.
     *
     * @param actor the actor using the token
     * @param map  the current game map
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = tool.activateSanctuaryEffect(actor, map, map.locationOf(actor));

        // Immediate turn-based flag
        actor.enableAbility(DamageInterceptor.PROTECTED);

        return result;
    }


    /**
     * Returns the menu description shown to the player.
     *
     * @param actor the actor holding the token
     * @return menu text
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " activates Heaven Token (deploys sanctuary field for " + FIELD_DURATION + " turns)";
    }
}
