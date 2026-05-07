package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.StealFromInventoryBehaviour;
import game.behaviours.WanderBehaviour;
import game.enums.Ability;

import java.util.ArrayList;
import java.util.List;

/**
 * HUNGRY STATE for CrazyChicken.
 * The chicken searches for workers with consumable items and steals from them.
 *
 * Transitions to:
 * - MIMICKING: if worker within 5 tiles
 * - WANDER: if no worker with consumable within 10 tiles
 * - stays HUNGRY: otherwise
 *
 * @author Aida
 */
public class HungryState implements State {
    private final StealFromInventoryBehaviour stealBehaviour = new StealFromInventoryBehaviour();
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int HUNGRY_DISTANCE = 10;
    private static final int MIMIC_PRIORITY_DISTANCE = 5;

    @Override
    public Action getAction(Actor actor, Location location) {
        // Try to steal from adjacent workers
        Action stealAction = stealBehaviour.operate(actor, location);
        if (stealAction != null) {
            return stealAction;
        }

        // Otherwise move towards workers with consumables (simplified: wander)
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();
        Location myLoc = location;

        boolean hasNearbyWorker = false;
        boolean hasWorkerWithConsumable = false;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location checkLoc = map.at(x, y);
                if (checkLoc.containsAnActor()) {
                    Actor target = checkLoc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        int dist = Math.abs(checkLoc.x() - myLoc.x()) + Math.abs(checkLoc.y() - myLoc.y());
                        if (dist <= MIMIC_PRIORITY_DISTANCE) {
                            hasNearbyWorker = true;
                        }
                        if (dist <= HUNGRY_DISTANCE && hasConsumableInInventory(target)) {
                            hasWorkerWithConsumable = true;
                        }
                    }
                }
            }
        }

        // Prioritize MIMICKING (as per requirements)
        if (hasNearbyWorker) {
            return ChickenState.MIMICKING;
        }

        // If no workers with consumables nearby, go back to wandering
        if (!hasWorkerWithConsumable) {
            return ChickenState.WANDER;
        }

        return ChickenState.HUNGRY;
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
        // IMMEDIATE EFFECT: The chicken's stomach growls loudly
        // All consumable items on the ground within 5 tiles are pulled toward the chicken
        GameMap map = location.map();

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLoc = map.at(x, y);
                int dist = Math.abs(x - location.x()) + Math.abs(y - location.y());
                if (dist <= 5) {
                    // Check for consumable items on the ground
                    List<Item> itemsToMove = new ArrayList<>();
                    for (Item item : targetLoc.getItems()) {
                        String name = item.toString().toLowerCase();
                        if (name.contains("apple") || name.contains("cookie") ||
                                name.contains("flask") || name.contains("first aid")) {
                            itemsToMove.add(item);
                        }
                    }
                    // Move each consumable item to the chicken's location
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