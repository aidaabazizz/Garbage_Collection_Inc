package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A status that causes player movement to be randomized.
 * Implements DisorientedCapability for capability pattern.
 *
 * @author Aida
 */
public class BlizzardDisorientationStatus implements Status, DisorientedCapability {
    private int remainingTurns;

    public BlizzardDisorientationStatus(int duration) {
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
    public boolean isDisoriented() {
        return remainingTurns > 0;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    @Override
    public String toString() {
        return "Blizzard disorientation (" + remainingTurns + " turns left)";
    }
}