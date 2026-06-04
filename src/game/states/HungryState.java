package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
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
 * The chicken is hungry and pulls consumable items toward it from a 5-tile radius.
 * Action: Steals and consumes consumables from adjacent workers (StealFromInventoryBehaviour) OR wanders
 * Transitions to:
 * MIMICKING: if a worker is within 5 tiles
 * WANDER: if no adjacent worker has a consumable item
 * stays HUNGRY: otherwise
 * On Enter: Pulls ALL consumable items within 5 tiles to chicken's location; displays "The ground trembles as items are pulled toward it!"
 *
 * @author Aida
 */
public class HungryState implements State<ChickenState> {
    private final StealFromInventoryBehaviour stealBehaviour = new StealFromInventoryBehaviour();
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private final Display display = new Display();
    private static final int MIMIC_PRIORITY_DISTANCE = 5;
    private static final int PULL_RADIUS = 5;

    @Override
    public Action getAction(Actor actor, Location location) {
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
        display.println("\u001B[33m" + actor + " is hungry! The ground trembles as items are pulled toward it!\u001B[0m");

        GameMap map = location.map();
        int pulledCount = 0;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLoc = map.at(x, y);
                int dist = Math.abs(x - location.x()) + Math.abs(y - location.y());

                if (dist <= PULL_RADIUS) {
                    List<Item> itemsToMove = new ArrayList<>();
                    for (Item item : targetLoc.getItems()) {
                        if (item.asCapability(Consumable.class).isPresent()) {
                            itemsToMove.add(item);
                            pulledCount++;
                        }
                    }
                    for (Item item : itemsToMove) {
                        targetLoc.removeItem(item);
                        location.addItem(item);
                    }
                }
            }
        }

        if (pulledCount > 0) {
            display.println("\u001B[33m" + pulledCount + " consumable items were pulled toward " + actor + "!\u001B[0m");
        }
    }

    @Override
    public void onExit(Actor actor, Location location) {
    }

    @Override
    public String getStateName() {
        return "HUNGRY";
    }
}