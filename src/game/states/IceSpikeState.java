// game/states/ElsaIceSpikeState.java
package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;
import game.enums.ElsaState;
import game.grounds.IceSpike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ICE SPIKE STATE for Elsa.
 *
 * @author Aida
 */
public class IceSpikeState implements State<ElsaState> {
    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private static final int ICE_SPIKE_DURATION = 3;
    private static final int SPIKE_RADIUS = 5;
    private static final int MAX_SPIKES = 8;

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
                        if (!(targetLoc.getGround() instanceof IceSpike)) {
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
        }
    }

    @Override
    public void onExit(Actor actor, Location location) {
        // Ice spikes will naturally melt via their own tick() method
    }

    @Override
    public String getStateName() {
        return "ICE SPIKE";
    }
}