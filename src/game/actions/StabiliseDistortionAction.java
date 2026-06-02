package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.sanctuary.DistortionSource;

public class StabiliseDistortionAction extends Action {
    private final Location targetLocation;

    public StabiliseDistortionAction(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        // Dependency Inversion Principle (DIP):
        // We depend on the interface DistortionSource, not concrete classes.
        DistortionSource source = targetLocation.getGroundAs(DistortionSource.class);

        if (source != null) {
            return actor + " stabilises the area: " + source.stabilise(targetLocation);
        }

        return actor + " failed to stabilise the area.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " stabilises the distortion at " + targetLocation.getGround();
    }
}
