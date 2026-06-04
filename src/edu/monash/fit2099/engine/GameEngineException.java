package edu.monash.fit2099.engine;

/**
 * A class to raise an exception from game engine.
 */
public class GameEngineException extends Exception{

    // Constructor that accepts a message
    public GameEngineException(String message){
        super(message);
    }

    @Override
    public String getMessage() {
        return "GAME ENGINE ERROR: " + super.getMessage() + "\n -- STOPS RUNNING --";
    }
}
