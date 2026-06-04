package edu.monash.fit2099.engine.positions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.ActorLocationsIterator;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.items.Item;

import java.util.*;

/**
 * Za Warudo! - DIO
 * Class representing the game world, including the locations of all Actors, the
 * player, and the playing grid.
 * @author Riordan Alfredo
 * @author Adrian Kristanto
 */
public abstract class World {
	protected Display display;
	protected ArrayList<GameMap> gameMaps = new ArrayList<>();
	protected ActorLocationsIterator actorLocations = new ActorLocationsIterator();
	protected List<Actor> players = new ArrayList<>();
	protected Map<Actor, Action> lastActionMap = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param display the Display that will display this World.
	 */
	public World(Display display) {
		Objects.requireNonNull(display);
		this.display = display;
	}

	/**
	 * Add a GameMap to the World.
	 * @param gameMap the GameMap to add
	 */
	public void addGameMap(GameMap gameMap) {
		Objects.requireNonNull(gameMap);
		gameMaps.add(gameMap);
		gameMap.actorLocations = actorLocations;
	}

	/**
	 * Set an actor as the player. The map is drawn just before this Actor's turn
	 * 
	 * @param player   the player to add
	 * @param location the Location where the player is to be added
	 */
	public void addPlayer(Actor player, Location location) throws GameEngineException {
		this.players.add(player);
		actorLocations.add(player, location);
		actorLocations.addPlayer(player);
	}

	/**
	 * Run the game.
	 *
	 * On each iteration the gameloop does the following: - displays the player's
	 * map - processes the actions of every Actor in the game, regardless of map
	 *
	 * We could either only process the actors on the current map, which would make
	 * time stop on the other maps, or we could process all the actors. We chose to
	 * process all the actors.
	 *
	 */
	public void run() {
		try{
			if (players.isEmpty())
				throw new IllegalStateException();

			// initialize the last action map to nothing actions;
			for (Actor actor : actorLocations) {
				lastActionMap.put(actor, new DoNothingAction());
			}

			// This loop is basically the whole game
			while (stillRunning()) {
				this.gameLoop();
			}
			display.println(endGameMessage());
		}
		catch (GameEngineException exception){
			display.println(exception.getMessage());
		}
	}

	protected void gameLoop() throws GameEngineException{
		// Tick over all the maps. For the map stuff.
		for (GameMap gameMap : gameMaps) {
			gameMap.tick();
		}

		// Process all the actors.
		for (Actor actor : actorLocations) {
			if (stillRunning()) {
				// if the current actor being processed is a player, draw the map where the actor is currently on
				// this is to avoid drawing all maps at the start, which may overwhelming and confusing for the player.
				if (players.contains(actor)) {
					GameMap gameMap = actorLocations.locationOf(actor).map();
					gameMap.draw(display);
				}
				processActorTurn(actor);
			}
		}
	}

	/**
	 * Gives an Actor its turn.
	 *
	 * The Actions an Actor can take include:
	 * <ul>
	 * <li>those conferred by items it is carrying</li>
	 * <li>movement actions for the current location and terrain</li>
	 * <li>actions that can be done to Actors in adjacent squares</li>
	 * <li>actions that can be done using items in the current location</li>
	 * <li>skipping a turn</li>
	 * </ul>
	 *
	 * @param actor the Actor whose turn it is.
	 */
	private void processActorTurn(Actor actor) {
		// before
		Location here = actorLocations.locationOf(actor); // prepare current location
		GameMap map = here.map(); //prepare the map
		ActionList actions = this.prepareAllowableActions(actor, here); //prepare all actions that this actor can do.

		// during: action selection
		Action action = actor.playTurn(actions, lastActionMap.get(actor), map, display); //get the action from the actor.

		// after
		lastActionMap.put(actor, action); // record selected action
		String result = action.execute(actor, map); // execute selected action
		display.println(result); // resolve.
	}


	/**
	 * A method to prepare all actions that are allowed by the actor at the corresponding location (here).
	 * @param actor actor that is
	 * @param here
	 * @return
	 */
	private ActionList prepareAllowableActions(Actor actor, Location here){
		ActionList actions = new ActionList();
		for (Item item : actor.getInventory().getItems()) {
			// for each item that can perform an action to its owner
			actions.add(item.allowableActions(actor, here.map()));
			// Game rule. If you're carrying it, you can drop it.
			actions.add(item.getDropAction(actor));
		}

		// Game rule. Allows the actor to interact with current ground
		actions.add(here.getGround().allowableActions(actor, here, ""));

		// get surrounding
		for (Exit exit : here.getExits()) {
			Location destination = exit.getDestination();

			if (actorLocations.isAnActorAt(destination)) {
				Actor otherActor = actorLocations.getActorAt(destination);
				actions.add(otherActor.allowableActions(actor, exit.getName(), here.map()));
				// for each item that allows the current actor perform an action to another actor
				for (Item item : actor.getInventory().getItems()) {
					actions.add(item.allowableActions(otherActor, destination));
				}
			} else {
				actions.add(destination.getGround().allowableActions(actor, destination, exit.getName()));
			}
			actions.add(destination.getMoveAction(actor, exit.getName(), exit.getHotKey()));
		}

		// get items on the current location
		for (Item item : here.getItems()) {
			// for each item that allows the actor to perform an action to it when it is on the ground
			actions.add(item.allowableActions(here));
			// Game rule. If it's on the ground you can pick it up.
			actions.add(item.getPickUpAction(actor));
		}

		// add an option to do nothing.
		actions.add(new DoNothingAction());

		return actions;
	}

	/**
	 * Returns true if the game is still running.
	 * The game is considered to still be running if any of the players is still around.
	 *
	 * @return true if any of the players is still on the map.
	 */
	protected boolean stillRunning() {
		for (Actor player : players) {
			if (actorLocations.contains(player))
				return true;
		}
		return false;
	}

	/**
	 * Return a string that can be displayed when the game ends.
	 *
	 * @return the string "Game Over"
	 */
	protected String endGameMessage() {
		return "Game Over";
	}
}
