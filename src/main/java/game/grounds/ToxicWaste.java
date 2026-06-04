package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A hazardous ground type representing toxic waste.
 * Toxic waste is a dangerous environmental hazard created when the Alien Cube corrupts
 * the source location after teleportation. Any actor standing on toxic wastes will damage them
 * by 1 health point per turn.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class ToxicWaste extends Ground {

    /**
     * The amount of damage dealt to actors standing on toxic waste per turn.
     */
    private final static int HURT = 1;

    /**
     * This constructs a new ToxicWaste ground title, so it initialises the ground
     * with display character and the name of the ground type.
     */
    public ToxicWaste() {
        super('≈', "Toxic Waste");
    }

    /**
     * If an actor is standing on toxic waste tile, they will incur the hurt.
     * @param location The location of the Ground
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.hurt(HURT);
        }
    }
}
