package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.capabilities.StatefulActor;
import game.enums.ChickenState;
import game.inventory.BasicInventory;
import game.states.*;
import game.weapons.CrazyChickenBeak;

/**
 * REQ5: A stateful creature that transitions between four distinct states.
 * Implements StatefulActor to allow state modifications without instanceof.
 *
 * @author Aida
 */
public class CrazyChicken extends NonPlayerCharacter implements StatefulActor {
    private static final int INITIAL_HEALTH = 30;
    private static final int BEAK_DAMAGE = 2;
    private static final int BEAK_HIT_RATE = 60;

    private State currentState;
    private ChickenState currentStateEnum;
    private int turnsInCurrentState = 0;
    private String currentStateName = "WANDER";

    // Store the beak as a field so we can replace it internally
    private CrazyChickenBeak currentBeak;

    public CrazyChicken() {
        super("CrazyChicken", 'ก', INITIAL_HEALTH, new BasicInventory());

        // Create and store the beak
        this.currentBeak = new CrazyChickenBeak(BEAK_DAMAGE, BEAK_HIT_RATE);

        // Initial state
        this.currentState = new WanderState();
        this.currentStateEnum = ChickenState.WANDER;
        this.currentStateName = "WANDER";
    }

    /**
     * Override getIntrinsicWeapon to return our current beak.
     */
    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return currentBeak;
    }

    /**
     * Replace the chicken's beak with a new one.
     * Implementation of StatefulActor interface.
     *
     * @param newBeak The new beak weapon to equip
     */
    @Override
    public void setBeak(CrazyChickenBeak newBeak) {
        this.currentBeak = newBeak;
    }

    /**
     * Get the current beak for stats inspection.
     * Implementation of StatefulActor interface.
     *
     * @return The current beak weapon
     */
    @Override
    public CrazyChickenBeak getBeak() {
        return currentBeak;
    }

    /**
     * Get the current state name.
     * Implementation of StatefulActor interface.
     *
     * @return The name of the current state
     */
    @Override
    public String getCurrentStateName() {
        return currentStateName;
    }

    /**
     * Set the current state name.
     * Implementation of StatefulActor interface.
     *
     * @param stateName The name of the new state
     */
    @Override
    public void setCurrentStateName(String stateName) {
        this.currentStateName = stateName;
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location currentLocation = map.locationOf(this);

        // Increment turn counter for current state
        turnsInCurrentState++;

        // Check for state transition
        ChickenState nextStateEnum = currentState.getNextState(this, currentLocation, turnsInCurrentState);

        if (nextStateEnum != currentStateEnum) {
            // State transition! Execute onExit of old state
            currentState.onExit(this, currentLocation);

            // Transition to new state
            switch (nextStateEnum) {
                case WANDER:
                    currentState = new WanderState();
                    currentStateName = "WANDER";
                    break;
                case MIMICKING:
                    currentState = new MimickingState();
                    currentStateName = "MIMICKING";
                    break;
                case FRENZY:
                    currentState = new FrenzyState();
                    currentStateName = "FRENZY";
                    break;
                case HUNGRY:
                    currentState = new HungryState();
                    currentStateName = "HUNGRY";
                    break;
            }
            currentStateEnum = nextStateEnum;
            turnsInCurrentState = 0;

            // Execute onEnter of new state (IMMEDIATE EFFECT)
            currentState.onEnter(this, currentLocation);

            // Display state change message
            display.println("\u001B[33m" + this + " enters " + currentStateName + " state!\u001B[0m");
        }

        // Get action from current state
        Action action = currentState.getAction(this, currentLocation);

        if (action != null) {
            return action;
        }

        return new DoNothingAction();
    }

    @Override
    public String toString() {
        return super.toString() + " [" + currentStateName + "]";
    }
}