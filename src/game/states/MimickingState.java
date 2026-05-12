// game/states/MimickingState.java
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.MirrorMovementBehaviour;
import game.capabilities.StatefulActor;
import game.capabilities.DisorientedStatus;
import game.enums.ChickenState;
import game.utils.SpatialSearch;

/**
 * MIMICKING STATE for CrazyChicken.
 *
 * @author Aida
 */
public class MimickingState implements State<ChickenState> {
    private MirrorMovementBehaviour mirrorBehaviour;
    private Actor trackedWorker;
    private static final int FRENZY_TRIGGER_TURNS = 2;
    private static final int MIMIC_DISTANCE = 5;

    @Override
    public Action getAction(Actor actor, Location location) {
        GameMap map = location.map();
        Actor nearestWorker = SpatialSearch.findNearestWorker(map, location);

        if (nearestWorker != null) {
            if (trackedWorker == null || trackedWorker != nearestWorker) {
                trackedWorker = nearestWorker;
                mirrorBehaviour = new MirrorMovementBehaviour(trackedWorker);
            }
            return mirrorBehaviour.operate(actor, location);
        }

        return new WanderingChicken().getAction(actor, location);
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();
        boolean hasNearbyWorker = SpatialSearch.hasWorkerWithinDistance(map, location, MIMIC_DISTANCE);

        if (!hasNearbyWorker) {
            return ChickenState.WANDER;
        }

        if (turnsInCurrentState >= FRENZY_TRIGGER_TURNS) {
            return ChickenState.FRENZY;
        }

        return ChickenState.MIMICKING;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        StatefulActor statefulActor = (StatefulActor) actor;
        statefulActor.setCurrentStateName("MIMICKING");

        // Use SpatialSearch.getNearbyWorkers() for adjacent workers!
        for (Actor worker : SpatialSearch.getNearbyWorkers(location)) {
            worker.addStatus(new DisorientedStatus(3));
        }
    }

    @Override
    public void onExit(Actor actor, Location location) {
        // No cleanup needed
    }

    @Override
    public String getStateName() {
        return "MIMICKING";
    }
}