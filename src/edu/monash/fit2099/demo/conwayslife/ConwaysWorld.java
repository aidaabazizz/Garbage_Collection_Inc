package edu.monash.fit2099.demo.conwayslife;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.World;

public class ConwaysWorld extends World {

    private static final int MAX_TURN = 50;
    private int numTurn = MAX_TURN;
    /**
     * Constructor.
     *
     */
    public ConwaysWorld(Display display) {
        super(display);
    }

    @Override
    protected final boolean stillRunning() {
        return (numTurn != 0);
    }

    @Override
    protected void gameLoop() throws GameEngineException {
        display.println("Turn's left: " + numTurn);
        super.gameLoop();
        numTurn--;
    }
}
