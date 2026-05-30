package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeSource;

public class GalvanicSurgeAction extends Action {
    private final ChargeSource source;
    private final String name;
    private final Location center;

    // Constructor for Items in Inventory (Use Actor as center)
    public GalvanicSurgeAction(ChargeSource source, String name) {
        this.source = source;
        this.name = name;
        this.center = null;
    }

    // Constructor for Stationary Ground (Use Ground as center)
    public GalvanicSurgeAction(ChargeSource source, String name, Location loc) {
        this.source = source;
        this.name = name;
        this.center = loc;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Display display = new Display();
        Location locationToTrigger = (this.center != null) ? this.center : map.locationOf(actor);
        source.releaseCharge(locationToTrigger, display, this.name);
        source.consumeSource(actor);
        return actor + " manually triggers the " + name + " pulse!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Manual Override: Trigger " + name;
    }
}
