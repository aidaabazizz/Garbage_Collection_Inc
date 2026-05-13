package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A status that prevents an actor from moving or taking actions.
 * Implements FreezableCapability for capability pattern.
 *
 * @author Aida
 */
public class FrozenStatus implements Status, FreezableCapability {
    private int remainingTurns;

    public FrozenStatus(int duration) {
        this.remainingTurns = duration;
    }

    @Override
    public void tickStatus(GameEntity entity, Location location) {
        remainingTurns--;
    }

    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    @Override
    public boolean isFrozen() {
        return remainingTurns > 0;
    }

    @Override
    public String toString() {
        return "Frozen in ice (" + remainingTurns + " turns left)";
    }
}