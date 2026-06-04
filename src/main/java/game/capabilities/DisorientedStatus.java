// game.capabilities/DisorientedStatus.java
package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A status that reduces the actor's accuracy/hit rate.
 *
 * @author Aida
 */
public class DisorientedStatus implements Status {
    private int remainingTurns;

    public DisorientedStatus(int turns) {
        this.remainingTurns = turns;
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
        return "Disoriented (" + remainingTurns + " turns left)";
    }
}