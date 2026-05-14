
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.WanderBehaviour;
import game.capabilities.Freezable;
import game.enums.Ability;
import game.enums.ElsaState;
import game.utils.ConsumableUseTracker;
import game.utils.SpatialSearch;

/**
 * Elsa releases a freezing wave across the entire map, immobilizing all workers.
 * Action: Moves randomly using WanderBehaviour
 * Transitions to:
 * ICE_SPIKE: if a consumable was used this round
 * SINGING: if a slime is adjacent
 * BLIZZARD: if 2+ workers within 8 tiles
 * WANDERING: if no workers within 3 tiles
 * stays FREEZE: otherwise
 * On Enter: Freezes ALL workers on the entire map for 2 turns; displays "releases a freezing wave across the whole map!"
 *
 * @author Aida
 * @version 1.0
 */
public class FreezeState implements State<ElsaState> {
    private static final int FREEZE_DISTANCE = 3;
    private static final int FREEZE_DURATION = 2;
    private static final int BLIZZARD_DISTANCE = 8;

    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private final Display display = new Display();

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ElsaState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();

        if (ConsumableUseTracker.consumeFlag()) {
            return ElsaState.ICE_SPIKE;
        }

        if (SpatialSearch.hasAdjacentSlime(location)) {
            return ElsaState.SINGING;
        }

        if (SpatialSearch.countWorkersWithinDistance(map, location, BLIZZARD_DISTANCE) >= 2) {
            return ElsaState.BLIZZARD;
        }

        if (!SpatialSearch.hasWorkerWithinDistance(map, location, FREEZE_DISTANCE)) {
            return ElsaState.WANDERING;
        }

        return ElsaState.FREEZE;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        GameMap map = location.map();

        display.println(actor + " releases a freezing wave across the whole map!");

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLocation = map.at(x, y);

                if (!targetLocation.containsAnActor()) {
                    continue;
                }

                Actor target = targetLocation.getActor();

                if (!target.hasAbility(Ability.WORKER)) {
                    continue;
                }

                target.asCapability(Freezable.class)
                        .ifPresent(freezable -> freezable.freeze(FREEZE_DURATION));

                display.println(target + " is frozen for " + FREEZE_DURATION + " turns.");
            }
        }
    }

    @Override
    public void onExit(Actor actor, Location location) {
        // Frozen status expires naturally.
    }

    @Override
    public String getStateName() {
        return "FREEZE";
    }
}