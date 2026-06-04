package edu.monash.fit2099.demo.mars.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import java.util.*;

public class KickAction extends Action {

	private final Actor target;
	private final Random rand = new Random();

	public KickAction(Actor target) {
		this.target = target;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		if (rand.nextBoolean()) {
			return target + " evades the clumsy kick.";
		} else {
			map.removeActor(target);
			return actor + " squashes " + target + " like a bug.";
		}
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor + " kicks " + target;
	}
}
