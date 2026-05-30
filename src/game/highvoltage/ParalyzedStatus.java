package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

public class ParalyzedStatus implements Status {
    private int remainingTurns;
    public ParalyzedStatus(int turns) {
        this.remainingTurns = turns; // normally just 1 turn of paralyzed
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
    public String toString() {
        return "Paralyzed (Reflective Surface)";
    }
}
