// game/states/ElsaSingingState.java
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.capabilities.Hypnotizable;
import game.enums.ElsaState;
import game.utils.MusicPlayer;

import java.util.Optional;

/**
 * SINGING STATE for Elsa.
 *
 * @author Aida
 */
public class SingingState implements State<ElsaState> {
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int SINGING_DURATION = 5;

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ElsaState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        if (turnsInCurrentState >= SINGING_DURATION) {
            return ElsaState.WANDERING;
        }
        return ElsaState.SINGING;
    }

    @Override
    public void onEnter(Actor actor, Location location) {

        System.out.println(actor + " starts singing Let It Go!");

        MusicPlayer.playMusic("src/game/music/let_it_go.wav");
        GameMap map = location.map();

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLoc = map.at(x, y);
                if (targetLoc.containsAnActor()) {
                    Actor target = targetLoc.getActor();
                    Optional<Hypnotizable> hypnotizable = target.asCapability(Hypnotizable.class);
                    if (hypnotizable.isPresent()) {
                        hypnotizable.get().hypnotize(SINGING_DURATION);
                    }
                }
            }
        }
    }


    @Override
    public void onExit(Actor actor, Location location) {
        // Statuses expire naturally
    }

    @Override
    public String getStateName() {
        return "SINGING";
    }
}