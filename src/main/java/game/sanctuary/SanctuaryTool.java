package game.sanctuary;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
/**
 * An interface for items or ground types that can be activated to produce
 * sanctuary-based effects.
 *
 * Implementing classes define unique behaviors that occur when an actor
 * uses the tool, such as creating protective zones or motivating allies.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public interface SanctuaryTool {
    /**
     * Triggers the specific effect of this sanctuary tool.
     *
     * This method uses the actor who initiated the action, the game map
     * where it occurred, and the specific location to modify the game world.
     * It returns a descriptive string summarizing the result of the activation.
     */
    String activateSanctuaryEffect(Actor actor, GameMap map, Location location);
}
