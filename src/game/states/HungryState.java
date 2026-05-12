// game/states/HungryState.java
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.StealFromInventoryBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.Consumable;
import game.enums.ChickenState;
import game.utils.SpatialSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * HUNGRY STATE for CrazyChicken.
 * Now checks for workers with consumables in ADJACENT tiles only.
 *
 * @author Aida
 */
public class HungryState implements State<ChickenState> {
    private final StealFromInventoryBehaviour stealBehaviour = new StealFromInventoryBehaviour();
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int MIMIC_PRIORITY_DISTANCE = 5;

    @Override
    public Action getAction(Actor actor, Location location) {
        // Try to steal from adjacent workers
        Action stealAction = stealBehaviour.operate(actor, location);
        if (stealAction != null) {
            return stealAction;
        }
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();

        boolean hasNearbyWorker = SpatialSearch.hasWorkerWithinDistance(map, location, MIMIC_PRIORITY_DISTANCE);
        // CHANGED: Now checks ADJACENT workers only for consumables
        boolean hasAdjacentWorkerWithConsumable = SpatialSearch.hasAdjacentWorkerWithConsumable(location);

        if (hasNearbyWorker) {
            return ChickenState.MIMICKING;
        }

        if (!hasAdjacentWorkerWithConsumable) {
            return ChickenState.WANDER;
        }

        return ChickenState.HUNGRY;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        // IMMEDIATE EFFECT: Pull consumable items within 5 tiles (this stays the same)
        GameMap map = location.map();

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLoc = map.at(x, y);
                int dist = Math.abs(x - location.x()) + Math.abs(y - location.y());
                if (dist <= 5) {
                    List<Item> itemsToMove = new ArrayList<>();
                    for (Item item : targetLoc.getItems()) {
                        if (item.asCapability(Consumable.class).isPresent()) {
                            itemsToMove.add(item);
                        }
                    }
                    for (Item item : itemsToMove) {
                        targetLoc.removeItem(item);
                        location.addItem(item);
                    }
                }
            }
        }
    }

    @Override
    public void onExit(Actor actor, Location location) {
        // No cleanup needed
    }

    @Override
    public String getStateName() {
        return "HUNGRY";
    }
}