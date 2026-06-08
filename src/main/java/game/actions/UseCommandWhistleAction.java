package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.MotivationStatus;
import game.enums.Ability;
import game.items.CommandWhistle;
import game.sanctuary.SanctuaryTool;
import game.utils.SpatialSearch;

/**
 * An action that activates the  CommandWhistle.
 *
 * Scans all actors adjacent to the whistle-user. If a friendly actor (WORKER)
 * is found nearby, that actor receives MotivationStatus. If none is found,
 * the whistle-user receives it instead. The status then fires an AoE knockback
 * pulse on its next tick.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class UseCommandWhistleAction extends Action {

    /**
     * The sanctuary tool responsible for executing whistle effects.
     */
    private final SanctuaryTool tool;

    /**
     * Constructs a new {@code UseCommandWhistleAction}.
     *
     * @param tool the sanctuary tool that handles whistle activation logic
     */
    public UseCommandWhistleAction(SanctuaryTool tool) {
        this.tool = tool;
    }

    /**
     * Finds the nearest friendly actor (or self if none), grants them
     * MotivationStatus, and lets the status handle knockback next tick.
     *
     * @param actor the actor using the whistle
     * @param map   the game map
     * @return result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return tool.activateSanctuaryEffect(actor, map, map.locationOf(actor));
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