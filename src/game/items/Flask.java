package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;

/**
 * A standard-issue hydration vessel for contracted workers.
 * The flask contains a limited number of charges and cannot be refilled
 * at puddles. It remains in the inventory even after all charges are spent.
 *
 * @author Jewell Gomes
 */
public class Flask extends Item implements Consumable {
    private static final int INITIAL_CAPACITY = 5;
    private static final int HEAL_POINTS = 1;
    private int totalUsable = INITIAL_CAPACITY;

    /**
     * Constructor for the Flask.
     * Assigns a weight of three units and a starting capacity of five charges.
     */
    public Flask() {
        super("Flask", 'u');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic((3)));
        this.makePortable();
    }

    /**
     * Processes the logic for drinking from the flask.
     * Heals the actor by one point per mouth full until empty.
     * @param actor The actor drinking from the flask.
     * @return A string indicating the healing result or if the vessel is empty.
     */
    @Override
    public String consumedBy(Actor actor) {
        if (totalUsable > 0) {
            totalUsable--;
            actor.heal(HEAL_POINTS);
            return String.format("%s drinks from flask and heals %d HP. (Charges left: %d)", actor, HEAL_POINTS, totalUsable);
        }
        return "The flask is empty.";
    }

    /**
     * Indicates if the flask has no charges remaining.
     * @return True if charges are zero, false otherwise.
     */
    @Override
    public boolean isFinished() { return totalUsable <= 0; }

    /**
     * Returns a list of actions allowing the actor to drink from the flask.
     * @param owner The actor carrying the flask.
     * @param map   The current game map.
     * @return A list of valid consume actions.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (totalUsable > 0) {
            actions.add(new ConsumeAction(this, this.toString()));
        }
        return actions;
    }

    /**
     * Displays the flask name and its current charge status.
     * @return A string representation of the flask.
     */
    @Override
    public String toString() {
        return super.toString() + (totalUsable > 0 ? " (" + totalUsable + " left)" : " (Empty)");
    }
}
