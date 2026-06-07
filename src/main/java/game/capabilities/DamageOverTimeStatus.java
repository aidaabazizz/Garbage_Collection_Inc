package game.capabilities;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

import java.util.Optional;

/**
 * An abstract representation of recurring negative effects on a game entity.
 * This class implements the logic for decrementing a statistic (typically health)
 * over a fixed number of game turns using the engine's status ticking system.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake (modified by)
 *
 */
public abstract class DamageOverTimeStatus implements Status {
    /**
     * The number of remaining turns before the status expires.
     */
    protected int remainingTurns;
    private final String statusName;
    private Display display = new Display();


    /**
     * Constructor to initialize the status parameters.
     *
     * @param statusName The identifier for the effect.
     * @param turns      The duration of the effect.
     */
    public DamageOverTimeStatus(String statusName, int turns) {
        this.statusName = statusName;
        this.remainingTurns = turns;
    }

    /**
     * Decreases the entity's health statistic during each status tick.
     *
     * @param entity   The entity affected by the status.
     * @param location The current location of the entity.
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        if (entity.hasStatistic(ActorStatistics.HEALTH) && entity.getStatistic(ActorStatistics.HEALTH) <= 0) {
            this.remainingTurns = 0; // Force the status to expire
            return;
        }

        if (entity.hasStatistic(ActorStatistics.HEALTH)) {
            entity.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, 1);

            display.println(entity + " takes 1 damage from " + statusName);
        }

        Optional<Actor> potentialActor = entity.asCapability(Actor.class);

        if (potentialActor.isPresent()) {
            Actor actor = potentialActor.get();
            // Check if the damage we just dealt killed them
            if (actor.getStatistic(ActorStatistics.HEALTH) <= 0) {
                // Trigger the engine's built-in removal logic
                String deathMessage = actor.unconscious(location.map());
                display.println("\u001B[31m" + deathMessage + "\u001B[0m");
            }
        }


        remainingTurns--;
    }

    /**
     * Checks if the effect is still operational based on remaining turns.
     *
     * @return True if turns remain; false if the effect has expired.
     */
    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    /**
     * @return A string representation including the remaining duration.
     */
    @Override
    public String toString() {
        return statusName + " (" + remainingTurns + " turns left)";
    }
}