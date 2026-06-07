package game.behaviours;


import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.Random;

/**
 * A behavior that causes an actor to move to a random adjacent location.
 * The behavior identifies all possible exits from the actor's current
 * position and filters for locations that the actor is permitted to enter.
 * One of these valid directions is then chosen at random.
 *
 * @author Jewell Gomes
 */
public class WanderBehaviour implements Behaviour<Actor, Action> {

    private final Random random = new Random();

    /**
     * Evaluates the surrounding exits and returns a random valid movement action.
     *
     * @param actor The actor performing the behavior.
     * @param location The current location of the actor.
     * @return A movement action to a random adjacent tile, or null if no valid exits are available.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        ArrayList<Action> actions = new ArrayList<>();

        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)) {
                actions.add(exit.getDestination().getMoveAction(actor, "around", exit.getHotKey()));
            }
        }

        if (!actions.isEmpty()) {
            return actions.get(random.nextInt(actions.size()));
        }
        else {
            return null;
        }
    }
}


