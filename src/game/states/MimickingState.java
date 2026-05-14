package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.MirrorMovementBehaviour;
import game.enums.ChickenState;
import game.utils.SpatialSearch;

/**
 * The chicken tracks a nearby worker and moves in the opposite direction.
 * Action: Mirrors worker movement (moves opposite direction)
 * Transitions to:
 * HUNGRY: if an adjacent worker has a consumable item
 * WANDER: if no worker within 5 tiles
 * FRENZY: after 2 turns (frenzy trigger duration ends)
 * stays MIMICKING: otherwise
 * On Enter: No immediate effect; starts tracking the nearest worker
 *
 * @author Aida
 * @version 1.0
 */
public class MimickingState implements State<ChickenState> {
    private static final int FRENZY_TRIGGER_TURNS = 2;
    private static final int MIMIC_DISTANCE = 5;

    private MirrorMovementBehaviour mirrorBehaviour;
    private Actor trackedWorker;

    @Override
    public Action getAction(Actor actor, Location location) {
        GameMap map = location.map();
        Actor nearestWorker = SpatialSearch.findNearestWorkerWithinDistance(map, location, MIMIC_DISTANCE);

        if (nearestWorker == null) {
            return null;
        }

        if (trackedWorker == null || trackedWorker != nearestWorker) {
            trackedWorker = nearestWorker;
            mirrorBehaviour = new MirrorMovementBehaviour(trackedWorker);
        }

        return mirrorBehaviour.operate(actor, location);
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();

        if (SpatialSearch.hasAdjacentWorkerWithConsumable(location)) {
            return ChickenState.HUNGRY;
        }

        if (!SpatialSearch.hasWorkerWithinDistance(map, location, MIMIC_DISTANCE)) {
            return ChickenState.WANDER;
        }

        if (turnsInCurrentState >= FRENZY_TRIGGER_TURNS) {
            return ChickenState.FRENZY;
        }

        return ChickenState.MIMICKING;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        // No immediate effect - just start mimicking
    }

    @Override
    public void onExit(Actor actor, Location location) {
        trackedWorker = null;
        mirrorBehaviour = null;
    }

    @Override
    public String getStateName() {
        return "MIMICKING";
    }
}