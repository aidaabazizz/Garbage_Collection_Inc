package edu.monash.fit2099.engine.actors;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.Location;

import java.util.*;

/**
 * Represents a bimap of locations and actors. Hence it enforces the one actor
 * per location rule. Has a nifty iterator that lets us modify the collection
 * while iterating it.
 * @author Riordan Alfredo
 */
public class ActorLocationsIterator implements Iterable<Actor> {
	/**
	 * A mapping of location to the actor
	 */
	private final Map<Location, Actor> locationToActor;
	/**
	 * A mapping of actor to location
	 */
	private final Map<Actor, Location> actorToLocation;
	/**
	 * Current actor
	 */
	private List<Actor> players;

	/**
	 * Default constructor.
	 */
	public ActorLocationsIterator() {
		locationToActor = new HashMap<Location, Actor>();
		actorToLocation = new HashMap<Actor, Location>();
		players = new ArrayList<>();
	}

	/**
	 * Adds an Actor and identifies it as the player. 
	 * Players always gets to go first in any turn.
	 * @param player the player
	 */
	public void addPlayer(Actor player) {
		this.players.add(player);
	}
			
			
	/**
	 * Add a new Actor at the given Location.
	 *
	 * @param actor the Actor to place
	 * @param location where to place the Actor
	 * @throws IllegalArgumentException if the Actor is already placed or there is already an Actor at the target Location
	 */
	public void add(Actor actor, Location location) throws GameEngineException {
		String gameRuleMessage = "Game rule, one actor per location. ";
		if(actorToLocation.containsKey(actor))
			throw new GameEngineException(gameRuleMessage + actor.toString() + " is already placed in the game.");
		if(locationToActor.containsKey(location))
			throw new GameEngineException(gameRuleMessage + "There is already an Actor at the " + location.toString());
		
		actorToLocation.put(actor, location);
		locationToActor.put(location, actor);
	}

	/**
	 * Remove an Actor from the system.
	 *
	 * @param actor the Actor to remove
	 */
	public void remove(Actor actor) {
		Location location = actorToLocation.get(actor);
		actorToLocation.remove(actor);
		locationToActor.remove(location);
	}

	/**
	 * Moves an existing Actor to a new Location.
	 *
	 * @param actor the Actor to move
	 * @param newLocation the Actor's destination
	 * @throws IllegalArgumentException if another Actor is already at that Location
	 */
	public void move(Actor actor, Location newLocation) {
		if(isAnActorAt(newLocation))
			throw new IllegalArgumentException("Can't move to another actor");

		Location oldLocation = actorToLocation.get(actor);
        if (oldLocation == null) {
            throw new IllegalArgumentException("Actor not found in the game");
        }
		actorToLocation.put(actor, newLocation);
		locationToActor.remove(oldLocation);
		locationToActor.put(newLocation, actor);
	}

	/**
	 * Returns true if actor exists in the system.
	 *
	 * @param actor the Actor to look for
	 * @return true if and only if actor is somewhere in the system
	 */
	public boolean contains(Actor actor) {
		return actorToLocation.containsKey(actor);
	}

	/**
	 * Returns true if an Actor is at the given Location.
	 *
	 * @param location the Location to check
	 * @return true if and only if an Actor is at the given Location.
	 */
	public boolean isAnActorAt(Location location) {
		return locationToActor.containsKey(location);
	}

	/**
	 * Returns a reference to the Actor at the given location, if there is one.
	 *
	 * @param location the location to check
	 * @return a reference to the Actor, or null if there isn't one 
	 */
	public Actor getActorAt(Location location) {
		return locationToActor.get(location);
	}

	/**
	 * Returns a reference to the Location containing the given Actor.
	 * @param actor the Actor to look for
	 * @return the Location containing actor
	 */
	public Location locationOf(Actor actor) {
		return actorToLocation.get(actor);
	}

	/**
	 * Class to allow iterating over all Actors in the system, player first
	 * This allows Actors to take turns in a known order.
	 */
	private class ActorIterator implements Iterator<Actor> {
		Map<Actor, Location> actorLocations;
		List<Actor> actors;

		/**
		 * Constructor.
		 * Creates a list of actors such that all players are prioritised, i.e., they can take their turns first.
		 * In other words, when an instance of ActorLocationsIterator is being iterated through (e.g., inside World's run method),
		 * the players will be first.
		 *
		 * @see edu.monash.fit2099.engine.positions.World#run()
		 * @param actorLocations the collection of mappings between Actors and Locations
		 */
		public ActorIterator(Map<Actor, Location> actorLocations) {
			this.actorLocations = actorLocations;
			actors = new ArrayList<>(actorLocations.keySet());

			// create a temporary array list since Java doesn't allow for a list to be iterated through and modified simultaneously
			List<Actor> playersFirstList = new ArrayList<>();
			// make sure that all players are at the front of the list so that they can take their turns first
			for (Actor player : players) {
				if (actors.contains(player)) {
					playersFirstList.add(player);
					actors.remove(player);
				}
			}
			// add the rest of non-playable characters
			playersFirstList.addAll(actors);
			actors = playersFirstList;
		}

		/**
		 * @see Iterator#hasNext()
		 * @return true if there is another Actor that needs to take a turn, false otherwise
		 */
		@Override
		public boolean hasNext() {
			for (Actor actor : actors) {
				if (actorLocations.containsKey(actor))
					return true;
			}

			return false;
		}

		/**
		 * @see Iterator#next()
		 */
		@Override
		public Actor next() {
			while (!actors.isEmpty()) {
				Actor actor = actors.remove(0);
				if (actorLocations.containsKey(actor))
					return actor;
			}

			throw new ConcurrentModificationException();
		}
	}

	/**
	 * @see Iterable#iterator()
	 */
	@Override
	public Iterator<Actor> iterator() {
		return new ActorIterator(actorToLocation);
	}
}
