// game/states/ElsaFreezeState.java
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
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
        GameMap map = location.map();

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLoc = map.at(x, y);
                if (targetLoc.containsAnActor()) {
                    Actor target = targetLoc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        Optional<Freezable> freezable = target.asCapability(Freezable.class);
                        if (freezable.isPresent()) {
                            freezable.get().freeze(FREEZE_DURATION);
                        }
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
        return "FREEZE";
    }
}