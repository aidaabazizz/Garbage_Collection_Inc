package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.capabilities.Freezable;
import game.enums.Ability;
import game.enums.ElsaState;

import java.util.Optional;

/**
 * FREEZE STATE for Elsa.
 *
 * @author Aida
 */
public class FreezeState implements State<ElsaState> {
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int FREEZE_DURATION = 2;
    private final Display display = new Display();

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ElsaState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        if (turnsInCurrentState >= FREEZE_DURATION) {
            return ElsaState.WANDERING;
        }
        return ElsaState.FREEZE;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        display.println("\u001B[36m" + actor + " raises her hands! A wave of ice spreads across the facility!\u001B[0m");

        GameMap map = location.map();
        int frozenCount = 0;

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLoc = map.at(x, y);
                if (targetLoc.containsAnActor()) {
                    Actor target = targetLoc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        Optional<Freezable> freezable = target.asCapability(Freezable.class);
                        if (freezable.isPresent()) {
                            freezable.get().freeze(FREEZE_DURATION);
                            frozenCount++;
                            display.println("\u001B[36m" + target + " is frozen in ice!\u001B[0m");
                        }
                    }
                }
            }
        }

        display.println("\u001B[36m" + frozenCount + " workers have been frozen for " + FREEZE_DURATION + " turns!\u001B[0m");
    }

    @Override
    public void onExit(Actor actor, Location location) {
        display.println("\u001B[36m" + actor + " lowers her hands. The ice begins to melt...\u001B[0m");
    }

    @Override
    public String getStateName() {
        return "FREEZE";
    }
}