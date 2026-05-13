// game/actors/Elsa.java
package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.capabilities.StatefulActor;
import game.enums.ElsaState;
import game.inventory.BasicInventory;
import game.states.*;
import game.weapons.CrazyChickenBeak;
import game.weapons.IceBlast;

/**
 * REQ5: A stateful ice-themed creature with five distinct states.
 *
 * @author Aida
 */
public class Elsa extends NonPlayerCharacter implements StatefulActor {
    private static final int INITIAL_HEALTH = 40;
    private static final int ICE_BLAST_DAMAGE = 3;
    private static final int ICE_BLAST_HIT_RATE = 70;

    private State<ElsaState> currentState;
    private ElsaState currentStateEnum;
    private int turnsInCurrentState = 0;
    private String currentStateName;

    private IceBlast currentWeapon;

    public Elsa() {
        super("Elsa", '☆', INITIAL_HEALTH, new BasicInventory());

        this.currentWeapon = new IceBlast(ICE_BLAST_DAMAGE, ICE_BLAST_HIT_RATE);

        this.currentState = new WanderingElsa();
        this.currentStateEnum = ElsaState.WANDERING;
        this.currentStateName = "WANDERING";
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return currentWeapon;
    }

    @Override
    public void setBeak(CrazyChickenBeak beak) {
        // Elsa doesn't use a beak
    }

    @Override
    public CrazyChickenBeak getBeak() {
        return null;
    }

    @Override
    public String getCurrentStateName() {
        return getCurrentStateName();
    }

    @Override
    public void setCurrentStateName(String stateName) {
        this.currentStateName = stateName;
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location currentLocation = map.locationOf(this);

        turnsInCurrentState++;

        ElsaState nextStateEnum = currentState.getNextState(this, currentLocation, turnsInCurrentState);

        if (nextStateEnum != currentStateEnum) {
            currentState.onExit(this, currentLocation);

            switch (nextStateEnum) {
                case WANDERING:
                    currentState = new WanderingElsa();
                    currentStateName = "WANDERING";
                    break;
                case FREEZE:
                    currentState = new FreezeState();
                    currentStateName = "FREEZE";
                    break;
                case BLIZZARD:
                    currentState = new BlizzardState();
                    currentStateName = "BLIZZARD";
                    break;
                case ICE_SPIKE:
                    currentState = new IceSpikeState();
                    currentStateName = "ICE SPIKE";
                    break;
                case SINGING:
                    currentState = new SingingState();
                    currentStateName = "SINGING";
                    break;
            }
            currentStateEnum = nextStateEnum;
            turnsInCurrentState = 0;

            currentState.onEnter(this, currentLocation);

            display.println("\u001B[36m" + this + " enters " + currentStateName + " state!\u001B[0m");
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