// game/states/ElsaBlizzardState.java
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.capabilities.Disorientable;
import game.enums.Ability;
import game.enums.ElsaState;

import java.util.Optional;

/**
 * BLIZZARD STATE for Elsa.
 *
 * @author Aida
 */
public class BlizzardState implements State<ElsaState> {
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int BLIZZARD_DURATION = 3;

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ElsaState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        if (turnsInCurrentState >= BLIZZARD_DURATION) {
            return ElsaState.WANDERING;
        }
        return ElsaState.BLIZZARD;
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
                        Optional<Disorientable> disorientable = target.asCapability(Disorientable.class);
                        if (disorientable.isPresent()) {
                            disorientable.get().disorient(BLIZZARD_DURATION);
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
        return "BLIZZARD";
    }
}