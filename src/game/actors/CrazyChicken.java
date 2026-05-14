package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.capabilities.BeakMutable;
import game.enums.ChickenState;
import game.inventory.BasicInventory;
import game.states.FrenzyState;
import game.states.HungryState;
import game.states.MimickingState;
import game.states.State;
import game.states.WanderingChicken;
import game.weapons.CrazyChickenBeak;

import java.util.EnumMap;
import java.util.Map;

/**
 * REQ5: A stateful creature that transitions between four deterministic states.
 *
 * @author Aida
 * @version 1.0
 */
public class CrazyChicken extends NonPlayerCharacter implements BeakMutable {
    private static final int INITIAL_HEALTH = 30;
    private static final int BEAK_DAMAGE = 2;
    private static final int BEAK_HIT_RATE = 60;

    private final Map<ChickenState, State<ChickenState>> states;
    private State<ChickenState> currentState;
    private ChickenState currentStateKey;
    private int turnsInCurrentState;
    private CrazyChickenBeak currentBeak;

    public CrazyChicken() {
        super("CrazyChicken", 'ก', INITIAL_HEALTH, new BasicInventory());

        this.currentBeak = new CrazyChickenBeak(BEAK_DAMAGE, BEAK_HIT_RATE);
        this.states = new EnumMap<>(ChickenState.class);

        registerStates();
        setInitialState(ChickenState.WANDER);
    }

    private void registerStates() {
        states.put(ChickenState.WANDER, new WanderingChicken());
        states.put(ChickenState.MIMICKING, new MimickingState());
        states.put(ChickenState.HUNGRY, new HungryState());
        states.put(ChickenState.FRENZY, new FrenzyState());
    }

    private void setInitialState(ChickenState stateKey) {
        this.currentStateKey = stateKey;
        this.currentState = states.get(stateKey);
        this.turnsInCurrentState = 0;
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location currentLocation = map.locationOf(this);
        turnsInCurrentState++;

        ChickenState nextStateKey = currentState.getNextState(this, currentLocation, turnsInCurrentState);

        if (nextStateKey != currentStateKey) {
            transitionTo(nextStateKey, currentLocation, display);
        }

        Action action = currentState.getAction(this, currentLocation);
        return action != null ? action : new DoNothingAction();
    }

    private void transitionTo(ChickenState nextStateKey, Location location, Display display) {
        currentState.onExit(this, location);

        currentStateKey = nextStateKey;
        currentState = states.get(nextStateKey);
        turnsInCurrentState = 0;

        currentState.onEnter(this, location);

        display.println("\u001B[33m" + this + " enters " + currentState.getStateName() + " state!\u001B[0m");
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return currentBeak;
    }

    public CrazyChickenBeak getBeak() {
        return currentBeak;
    }

    public void setBeak(CrazyChickenBeak currentBeak) {
        this.currentBeak = currentBeak;
    }

    @Override
    public String toString() {
        return super.toString() + " [" + currentState.getStateName() + "]";
    }
}