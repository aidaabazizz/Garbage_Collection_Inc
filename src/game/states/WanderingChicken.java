// game/states/WanderState.java (updated)
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.capabilities.Consumable;
import game.enums.Ability;
import game.enums.ChickenState;

/**
 * WANDER STATE for CrazyChicken.
 *
 * @author Aida
 */
public class WanderingChicken implements State<ChickenState> {
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
        boolean hasNearbyWorker = false;
        boolean hasWorkerWithConsumable = false;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location checkLoc = map.at(x, y);
                if (checkLoc.containsAnActor()) {
                    Actor target = checkLoc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        int dist = Math.abs(checkLoc.x() - location.x()) + Math.abs(checkLoc.y() - location.y());
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
            if (item.asCapability(Consumable.class).isPresent()) {
                return true;
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
        return "WANDER";
    }
}