package edu.monash.fit2099.engine.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An Action that doesn't do anything.  
 * Use this to implement waiting or similar actions in game clients.
 * @author Riordan Alfredo
 */
public class DoNothingAction extends Action {

	@Override
	public String execute(Actor actor, GameMap map) {
		return menuDescription(actor);
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor + " does nothing";
	}
	
	@Override
	public String hotkey() {
		return "5";
	}
}
