package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.enums.ElsaState;
import game.inventory.BasicInventory;
import game.states.BlizzardState;
import game.states.FreezeState;
import game.states.IceSpikeState;
import game.states.SingingState;
import game.states.State;
import game.states.WanderingElsa;
import game.weapons.IceBlast;

import java.util.EnumMap;
import java.util.Map;

/**
 * REQ5: A stateful ice-themed creature that transitions between five deterministic states.
 *
 * @author Aida
 * @version 1.0
 */
public class Elsa extends NonPlayerCharacter {
    private static final int INITIAL_HEALTH = 40;
    private static final int ICE_BLAST_DAMAGE = 3;
    private static final int ICE_BLAST_HIT_RATE = 70;

    private final Map<ElsaState, State<ElsaState>> states;
    private State<ElsaState> currentState;
    private ElsaState currentStateKey;
    private int turnsInCurrentState;
    private final IceBlast currentWeapon;

    public Elsa() {
        super("Elsa", '☆', INITIAL_HEALTH, new BasicInventory());

        this.currentWeapon = new IceBlast(ICE_BLAST_DAMAGE, ICE_BLAST_HIT_RATE);
        this.states = new EnumMap<>(ElsaState.class);

        registerStates();
        setInitialState(ElsaState.WANDERING);
    }

    private void registerStates() {
        states.put(ElsaState.WANDERING, new WanderingElsa());
        states.put(ElsaState.FREEZE, new FreezeState());
        states.put(ElsaState.BLIZZARD, new BlizzardState());
        states.put(ElsaState.ICE_SPIKE, new IceSpikeState());
        states.put(ElsaState.SINGING, new SingingState());
    }

    private void setInitialState(ElsaState stateKey) {
        this.currentStateKey = stateKey;
        this.currentState = states.get(stateKey);
        this.turnsInCurrentState = 0;
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return currentWeapon;
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location currentLocation = map.locationOf(this);
        turnsInCurrentState++;

        ElsaState nextStateKey = currentState.getNextState(this, currentLocation, turnsInCurrentState);

        if (nextStateKey != currentStateKey) {
            transitionTo(nextStateKey, currentLocation, display);
        }

        Action action = currentState.getAction(this, currentLocation);
        return action != null ? action : new DoNothingAction();
    }

    private void transitionTo(ElsaState nextStateKey, Location location, Display display) {
        currentState.onExit(this, location);

        currentStateKey = nextStateKey;
        currentState = states.get(nextStateKey);
        turnsInCurrentState = 0;

        display.println("\u001B[36m" + this + " enters " + currentState.getStateName() + " state!\u001B[0m");

        currentState.onEnter(this, location);
    }

    @Override
    public String toString() {
        return super.toString() + " [" + currentState.getStateName() + "]";
    }
}