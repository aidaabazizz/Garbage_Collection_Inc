package game.actions;

import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;


public class InvertedMoveAction extends MoveActorAction  {

    private final Location moveToLocation; // needed for wall-collision check

    public InvertedMoveAction(Location flippedLocation, String direction, String hotKey) {
        super(flippedLocation, direction, hotKey);
        this.moveToLocation = flippedLocation;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!moveToLocation.canActorEnter(actor)) {
            actor.hurt(1);
            return actor + " tries to move but the spatial inversion slams them into a wall! (1 damage)";
        }
        return super.execute(actor, map) +
                "\nSpatial distortion pulls " + actor + " in the opposite direction!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return super.menuDescription(actor) + " (Inverted)";
    }
}