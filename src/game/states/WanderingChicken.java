// game/states/WanderingChicken.java
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.enums.ChickenState;
import game.utils.SpatialSearch;

/**
 * WANDER STATE for CrazyChicken.
 *
 * @author Aida
 */
public class WanderingChicken implements State<ChickenState> {
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int MIMIC_TRIGGER_DISTANCE = 5;
    // No more HUNGRY_TRIGGER_DISTANCE - now checks adjacent only!

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();

        boolean hasNearbyWorker = SpatialSearch.hasWorkerWithinDistance(map, location, MIMIC_TRIGGER_DISTANCE);
        // CHANGED: Now checks ADJACENT workers only (not radius)
        boolean hasAdjacentWorkerWithConsumable = SpatialSearch.hasAdjacentWorkerWithConsumable(location);

        if (hasNearbyWorker) {
            return ChickenState.MIMICKING;
        }

        if (hasAdjacentWorkerWithConsumable) {
            return ChickenState.HUNGRY;
        }

        return ChickenState.WANDER;
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
        return "WANDER";
    }
}