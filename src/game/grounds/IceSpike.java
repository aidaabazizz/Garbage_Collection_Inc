package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.IceSpikeCapability;

/**
 * A temporary ground type created by Elsa's Ice Spike state.
 * Acts as an impassable wall for 3 rounds, then melts away.
 *
 * @author Aida
 */
public class IceSpike extends Ground implements IceSpikeCapability {
    private int turnsRemaining = 3;
    private final Ground originalGround;

    public IceSpike(Ground originalGround) {
        super('▲', "Ice Spike");
        this.originalGround = originalGround;
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

    @Override
    public char getDisplayChar() {
        return turnsRemaining > 0 ? '▲' : originalGround.getDisplayChar();
    }
}