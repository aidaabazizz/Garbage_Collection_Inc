package edu.monash.fit2099.demo.conwayslife;

import edu.monash.fit2099.engine.capabilities.Status;

public class Alive implements Status {
    /**
     * Indicates whether this status is still active.
     *
     * @return true if active, false otherwise
     */
    @Override
    public boolean isStatusActive() {
        return true; //permanent
    }
}
