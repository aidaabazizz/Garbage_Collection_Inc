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
 *
 * @author Aida
 */
public class CrazyChicken extends NonPlayerCharacter implements StatefulActor {
    private static final int INITIAL_HEALTH = 30;
    private static final int BEAK_DAMAGE = 2;
    private static final int BEAK_HIT_RATE = 60;

    private State<ChickenState> currentState;
    private ChickenState currentStateEnum;
    private int turnsInCurrentState = 0;
    private String currentStateName = "WANDER";

    private CrazyChickenBeak currentBeak;

    public CrazyChicken() {
        super("CrazyChicken", 'ก', INITIAL_HEALTH, new BasicInventory());

        this.currentBeak = new CrazyChickenBeak(BEAK_DAMAGE, BEAK_HIT_RATE);

        this.currentState = new WanderingChicken();
        this.currentStateEnum = ChickenState.WANDER;
        this.currentStateName = "WANDER";
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return currentBeak;
    }

    @Override
    public void setBeak(CrazyChickenBeak newBeak) {
        this.currentBeak = newBeak;
    }

    @Override
    public CrazyChickenBeak getBeak() {
        return currentBeak;
    }

    @Override
    public String getCurrentStateName() {
        return currentStateName;
    }

    @Override
    public void setCurrentStateName(String stateName) {
        this.currentStateName = stateName;
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location currentLocation = map.locationOf(this);

        turnsInCurrentState++;

        ChickenState nextStateEnum = currentState.getNextState(this, currentLocation, turnsInCurrentState);

        if (nextStateEnum != currentStateEnum) {
            currentState.onExit(this, currentLocation);

            switch (nextStateEnum) {
                case WANDER:
                    currentState = new WanderingChicken();
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

            currentState.onEnter(this, currentLocation);

            display.println("\u001B[33m" + this + " enters " + currentStateName + " state!\u001B[0m");
        }

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