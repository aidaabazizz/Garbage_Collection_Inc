package game.capabilities;

public class ReversedMovementStatus extends TimedStatus {
    public ReversedMovementStatus(int turns) { super(turns); }
    @Override public String toString() { return "Spatially Inverted (" + turns + ")"; }
}
