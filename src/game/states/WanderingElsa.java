// game/states/ElsaWanderingState.java
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.capabilities.Hypnotizable;
import game.enums.Ability;
import game.enums.ElsaState;

import java.util.Optional;

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
    private static final double ICE_SPIKE_HEALTH_THRESHOLD = 0.3;

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ElsaState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();
        int workerCount = 0;
        boolean hasNearbyWorker = false;
        boolean hasNearbySlime = false;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location checkLoc = map.at(x, y);
                if (checkLoc.containsAnActor()) {
                    Actor target = checkLoc.getActor();
                    int dist = Math.abs(x - location.x()) + Math.abs(y - location.y());

                    if (target.hasAbility(Ability.WORKER)) {
                        workerCount++;
                        if (dist <= FREEZE_DISTANCE) {
                            hasNearbyWorker = true;
                        }
                    }

                    if (dist <= SLIME_DISTANCE) {
                        Optional<Hypnotizable> hypnotizable = target.asCapability(Hypnotizable.class);
                        if (hypnotizable.isPresent()) {
                            hasNearbySlime = true;
                        }
                    }
                }
            }
        }

        int maxHealth = actor.getMaximumStatistic(ActorStatistics.HEALTH);
        int currentHealth = actor.getStatistic(ActorStatistics.HEALTH);
        if (currentHealth <= maxHealth * ICE_SPIKE_HEALTH_THRESHOLD) {
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