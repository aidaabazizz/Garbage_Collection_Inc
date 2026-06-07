package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.SanctuaryField;

import game.sanctuary.DamageInterceptor;
import game.sanctuary.SanctuaryTool;

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
     * {@link SanctuaryField} at the actor's location, and registers it with the
     * map so it ticks each turn independently.
     *
     * @param actor the actor using the token
     * @param map   the current game map
     * @return a description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = tool.activateSanctuaryEffect(actor, map, map.locationOf(actor));

        // Immediate turn-based flag
        actor.enableAbility(DamageInterceptor.PROTECTED);

        return result;
    }
//        actor.getInventory().remove(token);
//
//        Location here = map.locationOf(actor);
//        here.setGround(new SanctuaryField(FIELD_DURATION, here.getGround()));
//
//        // Set protection immediately — tick already ran this turn
//        actor.enableAbility(DamageInterceptor.PROTECTED);
//
//        // FIX: Ensure both 'actor' and 'FIELD_DURATION' are passed to match %s and %d
//        return String.format(
//                "%s uses the Heaven Token! A holy sanctuary field manifests for %d turns.\n",
//                        actor, FIELD_DURATION
//
//        );


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
