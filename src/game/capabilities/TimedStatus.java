package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

public abstract class TimedStatus implements Status {
    protected int turns;
    public TimedStatus(int turns) { this.turns = turns; }
    @Override public void tickStatus(GameEntity e, Location l) { turns--; }
    @Override public boolean isStatusActive() { return turns > 0; }
}