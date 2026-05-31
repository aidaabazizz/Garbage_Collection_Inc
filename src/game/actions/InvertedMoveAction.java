package game.actions;

import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;


public class InvertedMoveAction extends MoveActorAction  {

    /** The already-flipped destination this action will move the actor to. */
    private final Location flippedLocation;

    /**
     * Creates an InvertedMoveAction.
     *
     * @param flippedLocation the ALREADY FLIPPED destination
     * @param direction       the label shown in the menu (opposite direction)
     * @param hotKey          the hotkey for this action
     */
    public InvertedMoveAction(Location flippedLocation, String direction, String hotKey) {
        super(flippedLocation, direction, hotKey);
        this.flippedLocation = flippedLocation;
    }

    /**
     * Executes the inverted move and appends a distortion message.
     *
     * @param actor the actor being moved
     * @param map   the game map
     * @return result description
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (this.flippedLocation.canActorEnter(actor)) {
            map.moveActor(actor, this.flippedLocation);
            return actor + " is pulled through a spatial rift and ends up at " + this.flippedLocation + "!";
        } else {
            // COMPLEX EFFECT: If it's a wall, Bob stays put and takes damage
            actor.hurt(1);
            return actor + " tries to move, but the spatial inversion slams them into a wall! (1 Damage)";
        }
    }

    /**
     * Menu description clarifies the inversion to the player.
     *
     * @param actor the actor
     * @return menu text
     */
    @Override
    public String menuDescription(Actor actor) {
        return super.menuDescription(actor) + " (Inverted)";
    }

}