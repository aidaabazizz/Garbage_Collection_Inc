package edu.monash.fit2099.engine.behaviours;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Created by:
 * @author Riordan D. Alfredo
 * Modified by:
 * @author  Adrian Kristanto
 * @param <T> The type of entity executing the behaviour (e.g., Actor, Ground, Item)
 * @param <R> The type of result produced by the behaviour (e.g., Action)
 * An interface for creating behaviours.
 * Chaining these together can result in an entity (T) performing more complex tasks automatically.
 */
public interface Behaviour<T, R> {
	
	/**
	 * A Behaviour represents a kind of objective that an entity can have.  For example
	 * it might want to seek out a particular kind of object, or follow another entity,
	 * or run away and hide.  Each implementation of Behaviour helps the
	 * entity to achieve its objective (returning a result or null if no useful result are available).
	 * method that determines which Behaviour to perform.  This allows the Behaviour's logic
	 * to be reused in other Actors via delegation instead of inheritance.
	 * For example, an Actor(entity T)'s {@code playTurn()} method can use Behaviours to help decide which Action(result R) to
     * perform next.  It can also simply create Actions itself, and for simpler Actors this is
	 * likely to be sufficient.
	 * Using Behaviours allows us to modularise the code that decides what to do, and that means that it can be
	 * reused if (e.g.) more than one kind of Actor needs to be able to seek, follow, or hide.
	 *
	 * @author Riordan D. Alfredo
	 * @param entity The entity performing the behaviour
	 * @param location The location of the current entity
	 * @return The result of the behaviour, or null if no valid operation could be performed
	 */
	R operate(T entity, Location location);
}
