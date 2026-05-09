package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;
import game.capabilities.CreditHolder;
import game.capabilities.Infectable;
import game.capabilities.InfectionStatus;
import game.capabilities.Sellable;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.managers.CreatureSpawner;

/**
 * A multi-charge consumable item representing a pack of cookies.
 * Each charge provides either nutritional value or a health penalty
 * depending on the presence of a sterilization box. Cookies can also be
 * sold to the Supercomputer and infected by hostile environmental effects.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake
 * @author Suchir
 * @version 1.0
 */
public class Cookies extends Item implements Consumable, Sellable, Infectable {
    private static final int INITIAL_COUNT = 5;
    private static final int HEAL_POINTS = 1;
    private static final int MAX_HP_PENALTY = 1;
    private static final int WEIGHT = 2;

    private int count = INITIAL_COUNT;

    /**
     * Constructor for Cookies.
     * Sets the initial weight to two units and marks the item as portable.
     */
    public Cookies() {
        super("Cookies", '◍');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    /**
     * Processes the consumption of one cookie.
     *
     * @param actor the actor consuming the cookie
     * @return consumption effect description
     */
    @Override
    public String consumedBy(Actor actor) {
        if (count <= 0) {
            return "There are no cookies left.";
        }

        count--;

        if (actor.hasAbility(Ability.STERILIZER)) {
            actor.heal(HEAL_POINTS);
            return String.format("%s eats a sterilized cookie and heals %d HP.", actor, HEAL_POINTS);
        }

        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, MAX_HP_PENALTY);
        return String.format("%s eats an expired cookie. Max HP decreased by %d!", actor, MAX_HP_PENALTY);
    }

    /**
     * Checks whether all cookies are gone.
     *
     * @return true if no cookies remain
     */
    @Override
    public boolean isFinished() {
        return count <= 0;
    }

    /**
     * Removes the cookie pack if finished.
     *
     * @param actor the actor consuming the item
     * @param location the current location
     */
    @Override
    public void cleanUp(Actor actor, Location location) {
        if (isFinished()) {
            actor.getInventory().remove(this);
            location.removeItem(this);
        }
    }

    /**
     * Provides the consume action.
     *
     * @param owner the actor holding the cookies
     * @param map the current game map
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        if (count > 0) {
            actions.add(new ConsumeAction(this, this.toString()));
        }

        return actions;
    }

    /**
     * Gets the selling price based on remaining cookies.
     *
     * @return selling price
     */
    @Override
    public int getSellPrice() {
        return count;
    }

    /**
     * Applies the selling effect.
     *
     * @param seller the actor selling the item
     * @param map the current game map
     * @param wallet the seller's credit holder
     * @return selling effect description
     */
    @Override
    public String soldBy(Actor seller, GameMap map, CreditHolder wallet) {
        seller.hurt(count);
        return seller + " pays an organic processing fee of " + count + " health points.";
    }

    /**
     * Reacts to infection by adding an infection status to this item.
     *
     * @param location the location of the infected item
     */
    @Override
    public void reactToInfection(Location location) {
        this.addStatus(new InfectionStatus());
    }

    /**
     * Updates the infected cookie pack each turn.
     * The cookie count decreases and a parasite may be spawned nearby.
     *
     * @param location the location of the infected item
     */
    @Override
    public void updateInfection(Location location) {
        if (count > 0) {
            count--;
        }

        new CreatureSpawner().spawnParasite(location);

        if (count <= 0) {
            location.removeItem(this);
        }
    }

    /**
     * Returns the cookie display text.
     *
     * @return cookie description
     */
    @Override
    public String toString() {
        return super.toString() + " (" + count + " left)";
    }
}