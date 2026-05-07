package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.teleportstrategies.FixedTeleportStrategy;

import java.util.List;

public class TeleportationTube extends Ground {

    private final List<Location> destinations;
    public TeleportationTube(List<Location> destinations) {
        super('Φ', "Teleportation Tube");
        this.destinations = destinations;
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        actions.add(new TeleportAction(new FixedTeleportStrategy(destinations)));
        return actions;
    };
}
