package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.enums.ItemStatistics;
import game.actions.ConsumeAction;
import game.enums.Ability;
import game.capabilities.Consumable;

/**
 * A multi-charge consumable item representing a pack of cookies.
 * Each charge provides either nutritional value or a health penalty
 * depending on the presence of a sterilization box.
 *
 * @author Jewell Gomes
 */
public class Cookies extends Item implements Consumable {
    private static final int INITIAL_COUNT = 5;

    private static final int HEAL_POINTS = 1;

    private static final int MAX_HP_PENALTY = 1;

    private int count = INITIAL_COUNT;

    /**
     * Constructor for the Cookies.
     * Sets the initial weight to two units and marks the item as portable.
     */
    public Cookies() {
        super("Cookies", '◍');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(2));
        this.makePortable();
    }

    /**
     * Processes the consumption of a single cookie charge.
     * Decreases the consumer's maximum health if unsterilized, or restores
     * current health if sterilized.
     * @param actor The actor eating a cookie.
     * @return A description of the health changes.
     */
    @Override
    public String consumedBy(Actor actor) {
        count--;
        if (actor.hasAbility(Ability.STERILIZER)) {
            actor.heal(HEAL_POINTS);
            return String.format("%s eats a sterilized cookie and heals %d HP.", actor, HEAL_POINTS);
        } else {
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, MAX_HP_PENALTY);
            return String.format("%s eats an expired cookie. Max HP decreased by %d!", actor, MAX_HP_PENALTY);
        }
    }

    /**
     * Checks if all cookie charges have been depleted.
     * @return True if the count reaches zero, false otherwise.
     */
    @Override
    public boolean isFinished() {
        return count <= 0;
    }

    /**
     * Handles the removal of the cookie pack once all charges are used.
     * @param actor The actor who consumed the final charge.
     * @param location The map location of the actor.
     */
    @Override
    public void cleanUp(Actor actor, Location location) {
        if (this.isFinished()) {
            actor.getInventory().remove(this);
            location.removeItem(this);
        }
    }

    /**
     * Provides the action to consume one cookie from the pack.
     * @param owner The actor in possession of the cookies.
     * @param map The current game map.
     * @return A collection of valid actions.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this, this.toString()));
        return actions;
    }

    /**
     * Returns a string representation of the cookies including the remaining count.
     * @return The name of the item and its current charge count.
     */
    @Override
    public String toString() {
        return super.toString() + " (" + count + " left)";
    }
}
