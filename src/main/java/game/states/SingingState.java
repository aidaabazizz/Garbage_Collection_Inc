package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.WanderBehaviour;
import game.capabilities.Hypnotizable;
import game.enums.ElsaState;
import game.utils.ConsumableUseTracker;
import game.utils.MusicPlayer;
import game.utils.SpatialSearch;

import java.util.Optional;

/**
 * Elsa sings "Let It Go", hypnotizing all slimes on the map to attack workers.
 * Action: Moves randomly using WanderBehaviour
 * Transitions to:
 * ICE_SPIKE: if a consumable was used this round
 * FREEZE: if a worker is within 3 tiles
 * BLIZZARD: if 2+ workers within 8 tiles
 * WANDERING: after 5 turns OR no adjacent slime
 * stays SINGING: otherwise
 * On Enter: Hypnotizes ALL slimes on the entire map for 5 turns;
 * plays "Let It Go" music;
 * displays "The haunting melody echoes across the facility..."
 * On Exit: Slimes return to normal behavior;
 * displays "stops singing. The slimes shake their heads and return to normal."
 *
 * @author Aida
 * @version 1.0
 */
public class SingingState implements State<ElsaState> {
    private static final int FREEZE_DISTANCE = 3;
    private static final int BLIZZARD_DISTANCE = 8;
    private static final int SINGING_DURATION = 5;

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

        if (SpatialSearch.hasWorkerWithinDistance(map, location, FREEZE_DISTANCE)) {
            return ElsaState.FREEZE;
        }

        if (SpatialSearch.countWorkersWithinDistance(map, location, BLIZZARD_DISTANCE) >= 2) {
            return ElsaState.BLIZZARD;
        }

        if (turnsInCurrentState >= SINGING_DURATION || !SpatialSearch.hasAdjacentSlime(location)) {
            return ElsaState.WANDERING;
        }

        return ElsaState.SINGING;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        display.println("\u001B[ " + actor + " starts singing Let It Go! \u001B");
        display.println("\u001B[ The haunting melody echoes across the facility... \u001B");
        display.println("\u001B[ ALL the slimes seem to be in a trance! Their eyes glow with hunger! \u001B");

        MusicPlayer.playMusic("src/game/music/let_it_go.wav", display);

        GameMap map = location.map();
        int hypnotizedCount = 0;

        // Hypnotize ALL slimes on the ENTIRE MAP
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLoc = map.at(x, y);
                if (targetLoc.containsAnActor()) {
                    Actor target = targetLoc.getActor();
                    Optional<Hypnotizable> hypnotizable = target.asCapability(Hypnotizable.class);
                    if (hypnotizable.isPresent()) {
                        hypnotizable.get().hypnotize(SINGING_DURATION);
                        hypnotizedCount++;
                        display.println("\u001B[ " + target + " is hypnotized by Elsa's song! It will now attack any worker it sees!\u001B");
                    }
                }
            }
        }

        display.println("\u001B[ " + hypnotizedCount + " slime(s) have been hypnotized for " + SINGING_DURATION + " turns!\u001B");
    }

    @Override
    public void onExit(Actor actor, Location location) {
        display.println("\u001B[ " + actor + " stops singing. The slimes shake their heads and return to normal. \u001B");
    }

    @Override
    public String getStateName() {
        return "SINGING";
    }
}