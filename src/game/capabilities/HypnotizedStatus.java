package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Status that makes slimes hungry for workers.
 *
 * @author Aida
 */
public class HypnotizedStatus implements Status, HypnotizedCapability  {
    private int remainingTurns;

    public HypnotizedStatus(int duration) {
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

    public boolean isHypnotized() {
        return remainingTurns > 0;
    }

    @Override
    public String toString() {
        return "Hypnotized by Elsa's song (" + remainingTurns + " turns left)";
    }


}