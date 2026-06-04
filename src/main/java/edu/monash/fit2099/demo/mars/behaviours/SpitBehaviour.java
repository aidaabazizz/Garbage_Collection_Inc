package edu.monash.fit2099.demo.mars.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.NumberRange;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.behaviours.Behaviour;

import java.util.Random;

public class SpitBehaviour extends Action implements Behaviour<Actor, Action> {

	private final Actor target;

	public SpitBehaviour(Actor subject) {
		this.target = subject;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		return actor + " spits a horrible green slime at " + target + "." + System.lineSeparator()+
				"It's gross, but harmless.";
	}
	
	@Override
	public Action operate(Actor actor, Location here) {
		Random random = new Random();
		// 50% chance of not spitting
		if (random.nextBoolean()) {
			System.out.println(actor + " decides not to spit at " + target + ".");
			return null;
		}

		GameMap map = here.map();
		Location there = map.locationOf(target);

		NumberRange xs, ys;
		if (here.x() == there.x() || here.y() == there.y()) {
			xs = new NumberRange(Math.min(here.x(), there.x()), Math.abs(here.x() - there.x()) + 1);
			ys = new NumberRange(Math.min(here.y(), there.y()), Math.abs(here.y() - there.y()) + 1);

			for (int x : xs) {
				for (int y : ys) {
					if(map.at(x, y).getGround().blocksThrownObjects())
						return null;
				}
			}
			return this;
		}
		return null;
	}

	@Override
	public String menuDescription(Actor actor) {
		return "";
	}
}
