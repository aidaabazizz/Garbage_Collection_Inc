// game/grounds/IceSpike.java
package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.IceSpikeCapability;

/**
 * Temporary ground created by Elsa's ICE_SPIKE state.
 *
 * @author Aida
 * @version 1.0
 */
public class IceSpike extends Ground implements IceSpikeCapability {
    private int turnsRemaining;
    private final Ground originalGround;

    // Constructor requires 2 parameters: originalGround AND duration
    public IceSpike(Ground originalGround, int duration) {
        super('▲', "Ice Spike");
        this.originalGround = originalGround;
        this.turnsRemaining = duration;
    }

    @Override
    public void tick(Location location) {
        turnsRemaining--;

        if (turnsRemaining <= 0) {
            location.setGround(originalGround);
        }
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}