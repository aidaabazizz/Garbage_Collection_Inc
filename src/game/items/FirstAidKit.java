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
 * A vital medical item that provides permanent health benefits.
 * It features a mandatory cooldown period between uses to prevent
 * over-utilization.
 *
 * @author Jewell Gomes
 */
public class FirstAidKit extends Item implements Consumable {
    private static final int MAX_COOLDOWN = 20;
    private static final int MAX_HP_INCREASE = 1;
    private int cooldown = 0;

    /**
     * Constructor for the First Aid Kit.
     * Sets the item weight to twenty-five units and makes it portable.
     */
    public FirstAidKit() {
        super("First Aid Kit", '+');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(25));
        this.makePortable();
    }

    /**
     * Updates the cooldown timer for the kit.
     * The timer only advances if the kit is currently being carried by an actor.
     * @param currentLocation The location of the kit.
     * @param actor           The actor carrying the kit.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (actor != null  && cooldown < MAX_COOLDOWN) {
            cooldown++;
        }
    }

    /**
     * Executes the medical procedure logic.
     * Increases the consumer's maximum health and restores them to full health.
     * Slime creatures bypass the cooldown requirements.
     * @param actor The actor using the kit.
     * @return A string describing the medical outcome or cooldown status.
     */
    @Override
    public String consumedBy(Actor actor) {
        if (cooldown >= MAX_COOLDOWN) {
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, MAX_HP_INCREASE);
            actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.UPDATE, actor.getMaximumStatistic(ActorStatistics.HEALTH));
            this.cooldown = 0;
            return actor + " consumed the First Aid Kit! Max HP increased.";
        }

        if (actor.hasAbility(Ability.WORKER)) {
            return "First Aid Kit is on cooldown!";
        } else {
            return "ate the kit but it was non-functional.";
        }
    }

    /**
     * Determines if the kit is available for use in the action menu.
     * @param owner The actor holding the kit.
     * @param map   The game map.
     * @return A list containing the consume action if the cooldown is complete.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (cooldown >= MAX_COOLDOWN) {
            actions.add(new ConsumeAction(this, "First Aid Kit"));
        }
        return actions;
    }

    /**
     * Provides the item name and remaining cooldown turns if applicable.
     * @return A string describing the kit's current status.
     */
    @Override
    public String toString() {
        if (cooldown < MAX_COOLDOWN) {
            return super.toString() + " (Cooldown: " + (MAX_COOLDOWN - cooldown) + " left)";
        }
        return super.toString();
    }
}
