package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.MotivationStatus;
import game.enums.Ability;
import game.items.CommandWhistle;
import game.utils.SpatialSearch;

/**
 * An action that activates the {@link CommandWhistle}.
 *
 * <p>Scans all actors adjacent to the whistle-user. If a friendly actor (WORKER)
 * is found nearby, that actor receives {@link MotivationStatus}. If none is found,
 * the whistle-user receives it instead. The status then fires an AoE knockback
 * pulse on its next tick.</p>
 *
 * <p>Complex effect chain (Rule 2):
 * <pre>Item → Action → target scan → MotivationStatus applied
 *     → Status.tickStatus() → AoE knockback + wall-collision damage</pre>
 * </p>
 *
 * <p>Design: This action owns only "find target + apply status". All knockback
 * physics are in MotivationStatus (SRP). This action never references concrete
 * actor types — it queries Ability.WORKER capability (DIP).</p>
 *
 * @author Your Name
 */
public class UseCommandWhistleAction extends Action {

    /** The whistle item being used. */
    private final CommandWhistle whistle;

    /** Maximum tile radius to search for a friendly worker. */
    private static final int SEARCH_RADIUS = 5;

    /**
     * Creates an action to use the given whistle.
     *
     * @param whistle the whistle being used
     */
    public UseCommandWhistleAction(CommandWhistle whistle) {
        this.whistle = whistle;
    }

    /**
     * Finds the nearest friendly actor (or self if none), grants them
     * {@link MotivationStatus}, and lets the status handle knockback next tick.
     *
     * @param actor the actor using the whistle
     * @param map   the game map
     * @return result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);
        Actor target = findNearestFriendly(actor,here);

        if (target == null) {
            // No friendly found — apply to self
            target = actor;
        }

        // Apply motivation status — knockback fires on next tick
        target.addStatus(new MotivationStatus(map.locationOf(target)));

        return String.format(
                "%s blows the Command Whistle! %s is motivated — an AoE pulse will fire next turn!",
                actor, target
        );
    }

    /**
     * Scans all exits from the whistle-user's location for a friendly (WORKER) actor.
     * Uses Ability.WORKER capability check — no instanceof (DIP).
     *
     * @param user the actor using the whistle (excluded from search)
     * @param here the whistle-user's location
     * @return the first friendly actor found, or null if none
     */
    private Actor findNearestFriendly(Actor user, Location here) {
        Actor closest = null;
        int minDist = Integer.MAX_VALUE;

        for (int y : here.map().getYRange()) {
            for (int x : here.map().getXRange()) {
                Location loc = here.map().at(x, y);
                if (!loc.containsAnActor()) continue;
                Actor candidate = loc.getActor();
                if (candidate == user) continue;
                if (!candidate.hasAbility(Ability.WORKER)) continue;
                int dist = Math.abs(loc.x() - here.x()) + Math.abs(loc.y() - here.y());
                if (dist <= SEARCH_RADIUS && dist < minDist) {
                    minDist = dist;
                    closest = candidate;
                }
            }
        }
        return closest;
    }

    /**
     * Returns the menu text for this action.
     *
     * @param actor the actor using the whistle
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " blows the Command Whistle (AoE knockback pulse)";
    }
}