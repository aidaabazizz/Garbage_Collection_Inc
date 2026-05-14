package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.WanderBehaviour;
import game.capabilities.Disorientable;
import game.enums.Ability;
import game.enums.ElsaState;
import game.utils.ConsumableUseTracker;
import game.utils.SpatialSearch;

/**
 * Elsa summons a map-wide blizzard that disorients all workers, randomizing their movement.
 * Action: Moves randomly using WanderBehaviour
 * Transitions to:
 * ICE_SPIKE: if a consumable was used this round
 * SINGING: if a slime is adjacent
 * FREEZE: if a worker is within 3 tiles
 * WANDERING: after 3 turns AND less than 2 workers within 8 tiles
 * stays BLIZZARD: otherwise
 *
 * @author Aida
 * @version 1.0
 */
public class BlizzardState implements State<ElsaState> {
    private static final int FREEZE_DISTANCE = 3;
    private static final int BLIZZARD_DISTANCE = 8;
    private static final int BLIZZARD_DURATION = 3;

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

        if (SpatialSearch.hasWorkerWithinDistance(map, location, FREEZE_DISTANCE)) {
            return ElsaState.FREEZE;
        }

        if (turnsInCurrentState >= BLIZZARD_DURATION
                && SpatialSearch.countWorkersWithinDistance(map, location, BLIZZARD_DISTANCE) < 2) {
            return ElsaState.WANDERING;
        }

        return ElsaState.BLIZZARD;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        GameMap map = location.map();

        display.println(actor + " summons a blizzard!");

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

                target.asCapability(Disorientable.class)
                        .ifPresent(disorientable -> disorientable.disorient(BLIZZARD_DURATION));

                display.println(target + " is disoriented by the blizzard for "
                        + BLIZZARD_DURATION + " turns.");
            }
        }
    }

    @Override
    public void onExit(Actor actor, Location location) {
    }

    @Override
    public String getStateName() {
        return "BLIZZARD";
    }
}