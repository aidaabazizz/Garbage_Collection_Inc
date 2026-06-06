package edu.monash.fit2099.engine.positions;

import java.util.function.Supplier;

import edu.monash.fit2099.engine.GameEngineException;

/**
 * Interface for factory classes used by GameMap to create new map locations.
 * @author Riordan Alfredo
 * @author Adrian Kristanto
 */
public interface GroundCreator {
	void registerGround(char displayChar, Supplier<? extends Ground> groundSupplier) throws GameEngineException;
	
	Ground createGround(char displayChar) throws GameEngineException;
}
