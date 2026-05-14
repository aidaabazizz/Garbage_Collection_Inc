package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.WanderBehaviour;
import game.enums.ElsaState;
import game.grounds.IceSpike;
import game.utils.SpatialSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ICE_SPIKE state for Elsa.
 *
 * @author Aida
 * @version 1.0
 */
public class IceSpikeState implements State<ElsaState> {
    private static final int FREEZE_DISTANCE = 3;
    private static final int BLIZZARD_DISTANCE = 8;
    private static final int ICE_SPIKE_DURATION = 3;
    private static final int MAX_SPIKES = 8;

    private final WanderBehaviour wanderBehaviour = new WanderBehaviour();
    private final Display display = new Display();
    private final Random random = new Random();

    @Override
    public Action getAction(Actor actor, Location location) {
        return wanderBehaviour.operate(actor, location);
    }

    @Override
    public ElsaState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        GameMap map = location.map();

        if (SpatialSearch.hasAdjacentSlime(location)) {
            return ElsaState.SINGING;
        }

        if (SpatialSearch.hasWorkerWithinDistance(map, location, FREEZE_DISTANCE)) {
            return ElsaState.FREEZE;
        }

        if (SpatialSearch.countWorkersWithinDistance(map, location, BLIZZARD_DISTANCE) >= 2) {
            return ElsaState.BLIZZARD;
        }

        if (turnsInCurrentState >= ICE_SPIKE_DURATION) {
            return ElsaState.WANDERING;
        }

        return ElsaState.ICE_SPIKE;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        GameMap map = location.map();
        int spawned = 0;
        int attempts = 0;
        int maxAttempts = 100;

        List<Integer> xValues = new ArrayList<>();
        List<Integer> yValues = new ArrayList<>();

        for (int x : map.getXRange()) {
            xValues.add(x);
        }

        for (int y : map.getYRange()) {
            yValues.add(y);
        }

        display.println(actor + " summons random ice spikes across the map!");

        while (spawned < MAX_SPIKES && attempts < maxAttempts) {
            attempts++;

            int x = xValues.get(random.nextInt(xValues.size()));
            int y = yValues.get(random.nextInt(yValues.size()));

            Location targetLocation = map.at(x, y);

            if (targetLocation.containsAnActor()) {
                continue;
            }

            if (!targetLocation.canActorEnter(actor)) {
                continue;
            }

            // FIXED: Pass both parameters - Ground AND duration
            targetLocation.setGround(new IceSpike(targetLocation.getGround(), ICE_SPIKE_DURATION));
            spawned++;
        }

        display.println(spawned + " ice spikes rise in random locations.");
    }

    @Override
    public void onExit(Actor actor, Location location) {
        // IceSpike ground handles melting.
    }

    @Override
    public String getStateName() {
        return "ICE SPIKE";
    }
}