package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.enums.Ability;
import game.enums.ChickenState;

/**
 * WANDER STATE for CrazyChicken.
 * The chicken wanders aimlessly around the map.
 *
 * Transitions to:
 * - MIMICKING: if worker within 5 tiles
 * - HUNGRY: if worker with consumable within 10 tiles AND not within 5 tiles (mimicking prioritized)
 * - stays WANDER: otherwise
 *
 * @author Aida
 */
public class WanderState implements State {
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int MIMIC_TRIGGER_DISTANCE = 5;
    private static final int HUNGRY_TRIGGER_DISTANCE = 10;

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();
        Location myLoc = location;

        // Check for workers within MIMIC distance (5 tiles)
        boolean hasNearbyWorker = false;
        boolean hasWorkerWithConsumable = false;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location checkLoc = map.at(x, y);
                if (checkLoc.containsAnActor()) {
                    Actor target = checkLoc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        int dist = Math.abs(checkLoc.x() - myLoc.x()) + Math.abs(checkLoc.y() - myLoc.y());
                        if (dist <= MIMIC_TRIGGER_DISTANCE) {
                            hasNearbyWorker = true;
                        }
                        if (dist <= HUNGRY_TRIGGER_DISTANCE && hasConsumableInInventory(target)) {
                            hasWorkerWithConsumable = true;
                        }
                    }
                }
            }
        }

        // Prioritize MIMICKING over HUNGRY (as per requirements)
        if (hasNearbyWorker) {
            return ChickenState.MIMICKING;
        }

        if (hasWorkerWithConsumable) {
            return ChickenState.HUNGRY;
        }

        return ChickenState.WANDER;
    }

    private boolean hasConsumableInInventory(Actor actor) {
        for (Item item : actor.getInventory().getItems()) {
            String name = item.toString().toLowerCase();
            if (name.contains("apple") || name.contains("cookie") ||
                    name.contains("flask") || name.contains("first aid")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        // No immediate effect when entering wander state
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