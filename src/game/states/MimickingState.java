package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.MirrorMovementBehaviour;
import game.capabilities.DisorientedStatus;
import game.enums.Ability;

/**
 * MIMICKING STATE for CrazyChicken.
 * The chicken mirrors the movement of the nearest worker.
 *
 * Transitions to:
 * - FRENZY: after 2 or more turns in this state
 * - WANDER: if no worker within 5 tiles
 * - stays MIMICKING: otherwise
 *
 * @author Aida
 */
public class MimickingState implements State {
    private MirrorMovementBehaviour mirrorBehaviour;
    private Actor trackedWorker;
    private static final int FRENZY_TRIGGER_TURNS = 2;

    @Override
    public Action getAction(Actor actor, Location location) {
        // Find the nearest worker to track
        GameMap map = location.map();
        Actor nearestWorker = findNearestWorker(actor, location);

        if (nearestWorker != null) {
            if (trackedWorker == null || trackedWorker != nearestWorker) {
                trackedWorker = nearestWorker;
                mirrorBehaviour = new MirrorMovementBehaviour(trackedWorker);
            }
            return mirrorBehaviour.operate(actor, location);
        }

        // Fallback to wander if no worker found
        return new WanderState().getAction(actor, location);
    }

    private Actor findNearestWorker(Actor actor, Location location) {
        GameMap map = location.map();
        Actor closest = null;
        int minDist = Integer.MAX_VALUE;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor target = loc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        int dist = Math.abs(loc.x() - location.x()) + Math.abs(loc.y() - location.y());
                        if (dist < minDist) {
                            minDist = dist;
                            closest = target;
                        }
                    }
                }
            }
        }
        return closest;
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        // Check if still in mimicking range
        GameMap map = location.map();
        boolean hasNearbyWorker = false;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor() && loc.getActor().hasAbility(Ability.WORKER)) {
                    int dist = Math.abs(loc.x() - location.x()) + Math.abs(loc.y() - location.y());
                    if (dist <= 5) {
                        hasNearbyWorker = true;
                        break;
                    }
                }
            }
        }

        if (!hasNearbyWorker) {
            return ChickenState.WANDER;
        }

        // After 2 rounds of mimicking, go to FRENZY
        if (turnsInCurrentState >= FRENZY_TRIGGER_TURNS) {
            return ChickenState.FRENZY;
        }

        return ChickenState.MIMICKING;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        // IMMEDIATE EFFECT: The chicken lets out a mocking cackle
        // All adjacent workers become disoriented (add a status that reduces accuracy)
        GameMap map = location.map();
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor() && adj.getActor().hasAbility(Ability.WORKER)) {
                Actor worker = adj.getActor();
                // Add Disoriented status (reduces hit chance by 50% for 3 turns)
                worker.addStatus(new DisorientedStatus(3));
            }
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