package edu.monash.fit2099.engine.positions;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Class that can create different types of Ground based on the character that
 * represents it.
 * @author Riordan Alfredo
 * @author Adrian Kristanto
 */
public class DefaultGroundCreator implements GroundCreator {
	private final Map<Character, Supplier<? extends Ground>> map;

	/**
	 * Constructor.
	 * Initialises the map data structure that associates characters with their corresponding GroundInfo.
	 */
	public DefaultGroundCreator() {
		this.map = new HashMap<>();
	}

	/**
	 * Registers a display character to a ground supplier, which is a function that creates a new instance of the Ground.
	 * 
	 * @param displayChar character that represents this Ground in the game interface
	 * @param groundSupplier a supplier function that returns a new instance of a concrete subclass of Ground
	 * @throws GameEngineException if the character is already registered to another ground
	 */
	public void registerGround(char displayChar, Supplier<? extends Ground> groundSupplier) throws GameEngineException {
		if (map.containsKey(displayChar)) {
			throw new GameEngineException("Ground character '" + displayChar + "' is already registered.");
		}
		map.put(displayChar, groundSupplier);
	}

	/**
	 * Given a character, returns a new instance of the Ground type represented by it.
	 *
	 * @param displayChar character that represents this Ground in the UI
	 * @return an instance of a concrete subclass of Ground
	 */
	@Override
	public Ground createGround(char displayChar) throws GameEngineException {
		if (!map.containsKey(displayChar)) {
			throw new GameEngineException("Ground character '" + displayChar + "' is not registered.");
		}
		return map.get(displayChar).get();
	}
}