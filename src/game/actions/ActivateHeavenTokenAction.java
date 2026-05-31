package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.effects.SanctuaryField;
import game.items.HeavenToken;

public class ActivateHeavenTokenAction extends Action {
    /** The token to consume on use. */
    private final HeavenToken token;

    /** How many turns the sanctuary field lasts. */
    private static final int FIELD_DURATION = 10;

    /**
     * Creates an action to activate the given Heaven Token.
     *
     * @param token the token being used
     */
    public ActivateHeavenTokenAction(HeavenToken token) {
        this.token = token;
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
        // Consume the item (one-use)
        actor.getInventory().remove(token);

        // Replace the actor's current tile with SanctuaryField ground.
        // Ground.tick() is called every turn by GameMap.tick() automatically.
        Location here = map.locationOf(actor);
        here.setGround(new SanctuaryField(FIELD_DURATION, here.getGround()));

        return String.format(
                "%s uses the Heaven Token! A holy sanctuary field manifests for %d turns.\n" +
                        "Actors within 1 tile are protected from direct damage!",
                actor, FIELD_DURATION
        );
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
