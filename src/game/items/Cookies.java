package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Infectable;
import game.capabilities.InfectionStatus;
import game.enums.ItemStatistics;
import game.actions.ConsumeAction;
import game.enums.Ability;
import game.capabilities.Consumable;
import game.managers.CreatureSpawner;

/**
 * A multi-charge consumable item representing a pack of cookies.
 * Each charge provides either nutritional value or a health penalty
 * depending on the presence of a sterilization box.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake (Modified by)
 */
public class Cookies extends Item implements Consumable, Infectable {
    private static final int INITIAL_COUNT = 5;

    private static final int HEAL_POINTS = 1;

    private static final int MAX_HP_PENALTY = 1;

    private int count = INITIAL_COUNT;

    private static final int MAXIMUM_POINTS = 2;

    /**
     * Constructor for the Cookies.
     * Sets the initial weight to two units and marks the item as portable.
     */
    public Cookies() {
        super("Cookies", '◍');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(MAXIMUM_POINTS));
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


    //req4
    @Override
    public void reactToInfection(Location location) {
        // Add the status to this item so it starts ticking updateInfection
        this.addStatus(new InfectionStatus());
    }

    @Override
    public void updateInfection(Location location) {
        // 1. Reduces the Cookie's content by 1 each turn.
        if (count > 0) {
            count--;
        }

        // 2. Actively spawns other Parasites on adjacent tiles.
        // We do this every turn because the requirement says "actively spawns".
        spawnParasiteNearby(location);

        // 3. Cleanup logic: If the cookie is "consumed" by the infection, remove it.
        if (count <= 0) {
            location.removeItem(this);
        }
    }


     //Helper method to find an empty adjacent tile and spawn a parasite.
    private void spawnParasiteNearby(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            // "Standard Parasite spawning effect" is triggered inside spawnParasite()
            if (!destination.containsAnActor()) {
                new CreatureSpawner().spawnParasite(destination);
                return; // Requirement says "spawn a parasite", so we stop after one.
            }
        }
    }




}
