package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;

/**
 * An impassable structural element that blocks navigation.
 * Walls define the boundaries of the facility and cannot be entered
 * by any standard actor.
 *
 * @author Jewell Gomes
 */
public class Wall extends Ground {

    /**
     * Constructs a new Wall instance.
     * Initializes the wall with a display character of '#' and the display name "Wall".
     * The wall's primary characteristic is its impassable nature, which prevents
     * any actor from entering its tile.
     */
    public Wall() {
        super('#', "Wall");
    }

    /**
     * Prevents actors from moving onto the wall's location.
     * @param actor The actor attempting to enter.
     * @return Always false.
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}
