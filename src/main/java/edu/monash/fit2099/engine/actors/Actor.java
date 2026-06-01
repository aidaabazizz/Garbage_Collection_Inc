package edu.monash.fit2099.engine.actors;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Printable;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * The Actor class represents a {@link GameEntity} that can perform an
 * {@link Action}. It has a hit points attribute, which represents its health.
 * When this value reaches zero, the Actor is unconscious. Additionally, an
 * Actor has a collection of {@link Item}s it is carrying in its inventory.
 *
 * @author Riordan Alfredo
 * @author Adrian Kristanto
 */
public abstract class Actor extends GameEntity implements Printable {
    /**
     * Actor's name
     */
    protected final String name;
    /**
     * A character that will be displayed on the terminal/console -- remain the
     * same to minimise mutability.
     */
    private final char displayChar;

    /**
     * Natural/intrinsic weapon, like a punch
     */
    private IntrinsicWeapon intrinsicWeapon;

    private final Inventory inventory;

    /**
     * The constructor of the Actor class.
     *
     * @param name the name of the Actor
     * @param displayChar the character that will represent the Actor in the
     * display
     * @param hitPoints the Actor's starting hit points
     */
    public Actor(String name, char displayChar, int hitPoints, Inventory inventory) {
        this.name = name;
        this.displayChar = displayChar;
        this.inventory = inventory;
        this.addNewStatistic(ActorStatistics.HEALTH, new BaseStatistic(hitPoints));
    }

    /**
     * Get the actor's inventory
     * @return the actor's inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Select and return an action to perform on the current turn.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do
     * interesting things in conjunction with Action.getNextAction()
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the Action to be performed
     */
    public abstract Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display);

    /**
     * Returns a new collection of the Actions that the otherActor can do to the
     * current Actor.
     *
     * @param otherActor the Actor that might be performing attack
     * @param direction String representing the direction of the other Actor
     * @param map current GameMap
     * @return A collection of Actions.
     */
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        return new ActionList();
    }

    /**
     * Is this Actor conscious? Returns true if the current Actor has positive
     * hit points. Actors on zero hit points are deemed to be unconscious.
     * Depending on the game client, this status may be interpreted as either
     * unconsciousness or death, or inflict some other kind of status.
     *
     * @return true if and only if hitPoints is positive.
     */
    public boolean isConscious() {
        return this.getStatistic(ActorStatistics.HEALTH) > 0;
    }

    /**
     * Method that can be executed when the actor is unconscious due to the
     * action of another actor
     *
     * @param otherActor the perpetrator
     * @param map where the actor fell unconscious
     * @return a string describing what happened when the actor is unconscious
     */
    public String unconscious(Actor otherActor, GameMap map) {
        map.removeActor(this);
        return this + " met their demise in the hand of " + otherActor;
    }

    /**
     * Method that can be executed when the actor is unconscious due to natural
     * causes or accident For example, the actor is unconscious due to them
     * falling into a deep well.
     *
     * @param map where the actor fell unconscious
     * @return a string describing what happened when the actor is unconscious
     */
    public String unconscious(GameMap map) {
        map.removeActor(this);
        return this + " ceased to exist.";
    }

    /**
     * Hurt the current actor with the given damage points.
     *
     * @param damage the damage points that the actor receives
     */
    public void hurt(int damage) {
        this.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, damage);
    }

    /**
     * Heal the current actor with the given health points.
     *
     * @param points the health points that the actor receives
     */
    public void heal(int points) {
        this.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.INCREASE, points);
    }

    /**
     * Intrinsic weapon can be injected. This allows for more flexibility.
     *
     * @param intrinsicWeapon the intrinsic weapon of an actor, e.g. claw, bare
     * fist, etc.
     */
    protected void setIntrinsicWeapon(IntrinsicWeapon intrinsicWeapon) {
        this.intrinsicWeapon = intrinsicWeapon;
    }

    /**
     * Get intrinsic weapon.
     *
     * @return the intrinsic weapon of the current actor
     */
    public IntrinsicWeapon getIntrinsicWeapon() {
        return intrinsicWeapon;
    }

    /**
     * Returns true if and only if the current Actor has the required capability.
     * It will also return true if any of the items that the actor is carrying has the required capability.
     *
     * @param ability the ability required
     * @return true if and only if the current Actor has the required capability
     */
    @Override
    public final boolean hasAbility(Enum<?> ability) {
        for (Item item : getInventory().getItems()) {
            if (item.hasAbility(ability)) {
                return true;
            }
        }
        return super.hasAbility(ability);
    }

    /**
     * Returns the Actor's display character, so that it can be displayed in the
     * UI.
     *
     * @return the Actor's display character
     */
    @Override
    public char getDisplayChar() {
        return displayChar;
    }

    /**
     * A method for returning the string representation of an actor. It displays
     * the actor's name and its current hit points, along with its maximum
     * health hit points.
     */
    @Override
    public String toString() {
        return name + " ("
                + this.getStatistic(ActorStatistics.HEALTH) + "/"
                + this.getMaximumStatistic(ActorStatistics.HEALTH)
                + ")";
    }

}
