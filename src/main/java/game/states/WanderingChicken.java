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
 * The chicken wanders aimlessly, searching for workers or consumables.
 * Action: Moves randomly using WanderBehaviour
 * Transitions to:
 * HUNGRY: if an adjacent worker has a consumable item
 * MIMICKING: if a worker is within 5 tiles
 * stays WANDER: otherwise
 * On Enter: No immediate effect
 *
 * @author Aida
 */
public class WanderingChicken implements State<ChickenState> {
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int MIMIC_TRIGGER_DISTANCE = 5;

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();

        boolean hasNearbyWorker = SpatialSearch.hasWorkerWithinDistance(map, location, MIMIC_TRIGGER_DISTANCE);

        boolean hasAdjacentWorkerWithConsumable = SpatialSearch.hasAdjacentWorkerWithConsumable(location);

        if (hasAdjacentWorkerWithConsumable) {
            return ChickenState.HUNGRY;
        }

        if (hasNearbyWorker) {
            return ChickenState.MIMICKING;
        }

        return ChickenState.WANDER;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
    }

    @Override
    public void onExit(Actor actor, Location location) {
    }

    @Override
    public String getStateName() {
        return "WANDER";
    }
}