package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.capabilities.IceSpikeCapability;
import game.enums.ElsaState;
import game.grounds.IceSpike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ICE SPIKE STATE for Elsa.
 * Triggered when any worker's health drops to 50% or below.
 *
 * @author Aida
 */
public class IceSpikeState implements State<ElsaState> {
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int ICE_SPIKE_DURATION = 3;
    private static final int SPIKE_RADIUS = 5;
    private static final int MAX_SPIKES = 8;
    private final Display display = new Display();

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ElsaState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        if (turnsInCurrentState >= ICE_SPIKE_DURATION) {
            return ElsaState.WANDERING;
        }
        return ElsaState.ICE_SPIKE;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        display.println("\u001B[36m" + actor + " senses a wounded worker! Ice spikes erupt from the ground!\u001B[0m");

        GameMap map = location.map();
        List<Location> validLocations = new ArrayList<>();

        for (int y = -SPIKE_RADIUS; y <= SPIKE_RADIUS; y++) {
            for (int x = -SPIKE_RADIUS; x <= SPIKE_RADIUS; x++) {
                int checkX = location.x() + x;
                int checkY = location.y() + y;

                if (map.getXRange().contains(checkX) && map.getYRange().contains(checkY)) {
                    Location targetLoc = map.at(checkX, checkY);
                    int dist = Math.abs(x) + Math.abs(y);

                    if (dist <= SPIKE_RADIUS && dist > 0) {
                        // NO instanceof - using capability pattern!
                        if (targetLoc.getGroundAs(IceSpikeCapability.class) == null) {
                            validLocations.add(targetLoc);
                        }
                    }
                }
            }
        }

        Collections.shuffle(validLocations);
        int spikesToCreate = Math.min(MAX_SPIKES, validLocations.size());

        for (int i = 0; i < spikesToCreate; i++) {
            Location spikeLoc = validLocations.get(i);
            spikeLoc.setGround(new IceSpike(spikeLoc.getGround()));
            display.println("\u001B[36mAn ice spike erupts at " + spikeLoc + "!\u001B[0m");
        }

        display.println("\u001B[36m" + spikesToCreate + " ice spikes will remain for " + ICE_SPIKE_DURATION + " turns.\u001B[0m");
    }

    @Override
    public void onExit(Actor actor, Location location) {
        display.println("\u001B[36m" + actor + " calms down. The ice spikes begin to melt...\u001B[0m");
    }

    @Override
    public String getStateName() {
        return "ICE SPIKE";
    }
}