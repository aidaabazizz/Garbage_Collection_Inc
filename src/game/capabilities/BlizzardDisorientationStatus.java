package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

public class BlizzardDisorientationStatus extends TimedStatus {
    public BlizzardDisorientationStatus(int turns) { super(turns); }
    @Override public String toString() { return "Blizzard Disorientation (" + turns + ")"; }
}