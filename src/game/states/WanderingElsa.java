// game/states/WanderingElsa.java
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.enums.Ability;
import game.enums.ElsaState;
import game.utils.SpatialSearch;

/**
 * WANDERING STATE for Elsa.
 *
 * @author Aida
 */
public class WanderingElsa implements State<ElsaState> {
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int FREEZE_DISTANCE = 3;
    private static final int BLIZZARD_WORKER_COUNT = 5;
    private static final int SLIME_DISTANCE = 5;
    private static final double ICE_SPIKE_HEALTH_THRESHOLD = 0.5;  // Changed from 0.3 to 0.5 (50%)

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ElsaState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();

        int workerCount = SpatialSearch.countAllWorkers(map);
        boolean hasNearbyWorker = SpatialSearch.hasWorkerWithinDistance(map, location, FREEZE_DISTANCE);
        boolean hasNearbySlime = SpatialSearch.hasHypnotizableWithinDistance(map, location, SLIME_DISTANCE);

        // ========== NEW: Check if any worker has health ≤ 50% ==========
        boolean hasLowHealthWorker = hasWorkerWithLowHealth(map);
        // ==============================================================

        if (hasLowHealthWorker) {
            return ElsaState.ICE_SPIKE;
        }

        if (hasNearbyWorker) {
            return ElsaState.FREEZE;
        }

        if (workerCount >= BLIZZARD_WORKER_COUNT) {
            return ElsaState.BLIZZARD;
        }

        if (hasNearbySlime) {
            return ElsaState.SINGING;
        }

        return ElsaState.WANDERING;
    }

    /**
     * Checks if any worker on the map has health ≤ 50% of their maximum health.
     * Uses class comparison for status - NO instanceof!
     *
     * @param map The game map
     * @return true if any worker has low health, false otherwise
     */
    private boolean hasWorkerWithLowHealth(GameMap map) {
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        int maxHealth = target.getMaximumStatistic(ActorStatistics.HEALTH);
                        int currentHealth = target.getStatistic(ActorStatistics.HEALTH);
                        if (currentHealth <= maxHealth * ICE_SPIKE_HEALTH_THRESHOLD) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        // No immediate effect
    }

    @Override
    public void onExit(Actor actor, Location location) {
        // No cleanup needed
    }

    @Override
    public String getStateName() {
        return "WANDERING";
    }
}